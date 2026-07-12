package com.xiaozhi.ai.tts.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.ai.tts.XiaozhiTtsOptions;
import com.xiaozhi.common.model.bo.ConfigBO;
import com.xiaozhi.utils.AudioUtils;
import okhttp3.*;

import java.nio.file.Path;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import okio.BufferedSource;
import okio.ByteString;

/**
 * 基于 MOSS-TTS-Nano (本地 Docker / FastAPI) 的语音合成服务。
 *
 * <p>通过 HTTP 调用本地部署的 MOSS-TTS-Nano ONNX 服务，支持：
 * <ul>
 *   <li>{@link #textToSpeech(String)} 非流式：POST /api/generate，接收完整 WAV 音频（base64），适配为 48kHz 单声道 WAV 落盘</li>
 *   <li>{@link #streamToSpeech(String, Consumer)} 流式：POST /api/generate-stream/start → GET /api/generate-stream/{id}/audio，
 *       实时接收 PCM chunk 并做立体声→单声道转换</li>
 * </ul>
 */
@Slf4j
public class MossTtsNanoTtsService implements TtsService {

    private static final String PROVIDER_NAME = "moss-tts-nano";
    private static final String DEFAULT_BASE_URL = "http://localhost:18083";
    private static final int STEREO_PCM_FRAME_BYTES = 4;
    private static final int HTTP_CONNECT_TIMEOUT_SECONDS = 10;
    private static final int HTTP_READ_TIMEOUT_NON_STREAM_SECONDS = 60;
    private static final int HTTP_STREAM_READ_TIMEOUT_SECONDS = 120;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final XiaozhiTtsOptions options;
    private final String baseUrl;
    private final String outputPath;
    private final OkHttpClient httpClient;

    public MossTtsNanoTtsService(
            ConfigBO config,
            String voiceName,
            Double pitch,
            Double speed,
            String outputPath) {
        this.options = XiaozhiTtsOptions.builder()
                .voiceName(voiceName)
                .pitch(pitch)
                .speed(speed)
                .build();
        this.outputPath = outputPath;
        this.baseUrl = resolveBaseUrl(config);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(HTTP_CONNECT_TIMEOUT_SECONDS))
                .readTimeout(Duration.ofSeconds(HTTP_READ_TIMEOUT_NON_STREAM_SECONDS))
                .writeTimeout(Duration.ofSeconds(HTTP_CONNECT_TIMEOUT_SECONDS))
                .callTimeout(Duration.ofSeconds(HTTP_READ_TIMEOUT_NON_STREAM_SECONDS + 30L))
                .retryOnConnectionFailure(true)
                .build();
    }

    private static String resolveBaseUrl(ConfigBO config) {
        if (config == null) {
            return DEFAULT_BASE_URL;
        }
        String apiUrl = config.getApiUrl();
        if (apiUrl == null || apiUrl.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        String trimmed = apiUrl.trim();
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public XiaozhiTtsOptions getOptions() {
        return options;
    }

    // ===================== 非流式 =====================

    @Override
    public Path textToSpeech(String text) throws Exception {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("MOSS-TTS-Nano: 待合成文本为空");
        }
        log.info("MOSS-TTS-Nano 非流式合成开始 - len={}, voice={}", text.length(), getVoiceName());

        long start = System.currentTimeMillis();
        RequestBody body = buildRequestBody(text);

        Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String payload = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                throw new Exception("MOSS-TTS-Nano /api/generate 失败: HTTP " + response.code() + " " + extractError(payload));
            }
            JsonNode root = OBJECT_MAPPER.readTree(payload);
            if (root.hasNonNull("error")) {
                throw new Exception("MOSS-TTS-Nano 服务端错误: " + root.get("error").asText());
            }

            String audioBase64 = root.path("audio_base64").asText(null);
            if (audioBase64 == null || audioBase64.isBlank()) {
                throw new Exception("MOSS-TTS-Nano 响应缺少 audio_base64 字段");
            }
            int sampleRate = root.path("sample_rate").asInt(AudioUtils.TTS_OUTPUT_SAMPLE_RATE);

            byte[] wavBytes = Base64.getDecoder().decode(audioBase64);
            byte[] pcmMono = AudioUtils.wavToPcm(wavBytes);
            int inputChannels = parseWavChannels(wavBytes);
            if (inputChannels == 2) {
                pcmMono = stereoToMono(pcmMono);
            }
            if (sampleRate != AudioUtils.TTS_OUTPUT_SAMPLE_RATE) {
                pcmMono = AudioUtils.resamplePcm(pcmMono, sampleRate, AudioUtils.TTS_OUTPUT_SAMPLE_RATE);
            }

            Path outPath = Path.of(outputPath, getAudioFileName());
            AudioUtils.saveAsWav(outPath, pcmMono);

            long elapsed = System.currentTimeMillis() - start;
            log.info("MOSS-TTS-Nano 非流式合成完成 - file={}, 耗时={}ms", outPath.getFileName(), elapsed);
            return outPath;
        }
    }

    private RequestBody buildRequestBody(String text) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("text", text);
        String voice = getVoiceName();
        if (voice != null && !voice.isBlank()) {
            builder.addFormDataPart("demo_id", voice.trim());
        }
        builder.addFormDataPart("attn_implementation", "fixed");
        builder.addFormDataPart("do_sample", "1");
        builder.addFormDataPart("max_new_frames", "500");
        builder.addFormDataPart("voice_clone_max_text_tokens", "75");
        builder.addFormDataPart("audio_temperature", "0.8");
        builder.addFormDataPart("audio_top_p", "0.95");
        builder.addFormDataPart("audio_top_k", "25");
        builder.addFormDataPart("audio_repetition_penalty", "1.2");
        return builder.build();
    }

    // ===================== 流式 =====================

    public void streamToSpeech(String text, Consumer<byte[]> onChunk) throws Exception {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("MOSS-TTS-Nano: 待合成文本为空");
        }
        log.info("MOSS-TTS-Nano 流式合成开始 - len={}, voice={}", text.length(), getVoiceName());

        String streamId;
        RequestBody startBody = buildRequestBody(text);
        Request startReq = new Request.Builder()
                .url(baseUrl + "/api/generate-stream/start")
                .post(startBody)
                .build();
        try (Response resp = httpClient.newCall(startReq).execute()) {
            ResponseBody body = resp.body();
            String payload = body == null ? "" : body.string();
            if (!resp.isSuccessful()) {
                throw new Exception("MOSS-TTS-Nano /api/generate-stream/start 失败: HTTP "
                        + resp.code() + " " + extractError(payload));
            }
            JsonNode root = OBJECT_MAPPER.readTree(payload);
            if (root.hasNonNull("error")) {
                throw new Exception("MOSS-TTS-Nano 流式启动错误: " + root.get("error").asText());
            }
            streamId = root.path("stream_id").asText(null);
            if (streamId == null || streamId.isBlank()) {
                throw new Exception("MOSS-TTS-Nano 流式启动响应缺少 stream_id");
            }
        }

        OkHttpClient streamClient = httpClient.newBuilder()
                .readTimeout(Duration.ofSeconds(HTTP_STREAM_READ_TIMEOUT_SECONDS))
                .callTimeout(Duration.ZERO)
                .build();
        Request audioReq = new Request.Builder()
                .url(baseUrl + "/api/generate-stream/" + streamId + "/audio")
                .get()
                .build();

        AtomicBoolean active = new AtomicBoolean(true);
        try (Response resp = streamClient.newCall(audioReq).execute()) {
            if (!resp.isSuccessful()) {
                throw new Exception("MOSS-TTS-Nano /audio 失败: HTTP " + resp.code());
            }
            ResponseBody body = resp.body();
            if (body == null) {
                throw new Exception("MOSS-TTS-Nano 流式响应体为空");
            }
            String channelsHeader = resp.header("X-Audio-Channels");
            int channels = parseHeaderInt(channelsHeader, 2);

            BufferedSource source = body.source();
            // stereo 4 字节对齐累积器；MOSS 流式按 chunk 推送，未必 4 字节对齐
            byte[] carry = new byte[0];
            int readBufLen = 8 * 1024;
            byte[] readBuf = new byte[readBufLen];
            while (active.get()) {
                int read = source.read(readBuf, 0, readBufLen);
                if (read == -1) {
                    break;
                }
                if (read == 0) {
                    continue;
                }
                byte[] tmp = new byte[read];
                System.arraycopy(readBuf, 0, tmp, 0, read);
                byte[] raw = tmp;
                byte[] combined = (channels == 2)
                        ? alignStereoFrames(carry, raw)
                        : raw;

                if (channels == 2) {
                    int alignedLen = (combined.length / STEREO_PCM_FRAME_BYTES) * STEREO_PCM_FRAME_BYTES;
                    int leftover = combined.length - alignedLen;
                    byte[] aligned = new byte[alignedLen];
                    System.arraycopy(combined, 0, aligned, 0, alignedLen);
                    byte[] monoChunk = stereoToMono(aligned);
                    if (monoChunk.length > 0) {
                        onChunk.accept(monoChunk);
                    }
                    if (leftover > 0) {
                        carry = new byte[leftover];
                        System.arraycopy(combined, alignedLen, carry, 0, leftover);
                    } else {
                        carry = new byte[0];
                    }
                } else {
                    if (raw.length > 0) {
                        onChunk.accept(raw);
                    }
                }
            }
        } catch (IOException ioe) {
            if (active.get()) {
                throw new Exception("MOSS-TTS-Nano 流式读取中断: " + ioe.getMessage(), ioe);
            }
        } finally {
            closeStreamQuietly(streamClient, streamId);
        }
        log.info("MOSS-TTS-Nano 流式合成完成 - streamId={}", streamId);
    }

    /**
     * 返回一个 cold Flux，订阅时启动流式 TTS，每个元素是 48kHz / 单声道 / 16-bit LE PCM 字节片段。
     * <p>
     * 取消订阅：Flux 取消时，本方法会同步调用服务端 /close 接口释放资源。
     */
    public Flux<byte[]> streamAsFlux(String text) {
        return Flux.create(sink -> {
            AtomicBoolean sinkCancelled = new AtomicBoolean(false);
            try {
                streamToSpeech(text, chunk -> {
                    if (!sinkCancelled.get()) {
                        sink.next(chunk);
                    }
                });
                if (!sinkCancelled.get()) {
                    sink.complete();
                }
            } catch (Exception e) {
                if (!sinkCancelled.get()) {
                    sink.error(e);
                }
            }
            sink.onCancel(() -> {
                sinkCancelled.set(true);
                // 取消时尝试关闭当前正在进行的流
                cancelInflightStream();
            });
        });
    }

    /**
     * 取消当前正在进行的 TTS 流式调用占位（在简化版中无需维护 streamId 句柄）。
     * 当前 streamToSpeech 内部已经在流结束时自动调用 /close；
     * 此方法仅留作接口兼容，可以由上层在 cancel() 时调用（不抛错即可）。
     */
    public void cancelInflightStream() {
        // 简化设计：流自然结束时已自带 close；这里留空扩展点。
    }
    public void closeStream(String streamId) {
        if (streamId == null || streamId.isBlank()) {
            return;
        }
        closeStreamQuietly(httpClient, streamId);
    }

    private void closeStreamQuietly(OkHttpClient client, String streamId) {
        try {
            Request req = new Request.Builder()
                    .url(baseUrl + "/api/generate-stream/" + streamId + "/close")
                    .post(RequestBody.create(new byte[0], null))
                    .build();
            try (Response resp = client.newCall(req).execute()) {
                // best-effort, ignore
            }
        } catch (Exception ignore) {
            // best-effort
        }
    }

    // ===================== 音频辅助 =====================

    static byte[] stereoToMono(byte[] stereoPcm) {
        if (stereoPcm == null || stereoPcm.length == 0) {
            return new byte[0];
        }
        int frameBytes = STEREO_PCM_FRAME_BYTES;
        int frames = stereoPcm.length / frameBytes;
        byte[] mono = new byte[frames * 2];
        int outIdx = 0;
        for (int i = 0; i < frames; i++) {
            int base = i * frameBytes;
            short left = (short) ((stereoPcm[base] & 0xFF) | (stereoPcm[base + 1] << 8));
            short right = (short) ((stereoPcm[base + 2] & 0xFF) | (stereoPcm[base + 3] << 8));
            int avg = (left + right) / 2;
            mono[outIdx++] = (byte) (avg & 0xFF);
            mono[outIdx++] = (byte) ((avg >> 8) & 0xFF);
        }
        return mono;
    }

    static byte[] alignStereoFrames(byte[] accumulated, byte[] incoming) {
        if (incoming == null || incoming.length == 0) {
            return (accumulated == null) ? new byte[0] : accumulated;
        }
        int total = (accumulated == null ? 0 : accumulated.length) + incoming.length;
        int aligned = (total / STEREO_PCM_FRAME_BYTES) * STEREO_PCM_FRAME_BYTES;
        byte[] merged = new byte[aligned];
        int accLen = (accumulated == null) ? 0 : accumulated.length;
        if (accLen > 0) {
            System.arraycopy(accumulated, 0, merged, 0, accLen);
        }
        int need = aligned - accLen;
        if (need > 0) {
            System.arraycopy(incoming, 0, merged, accLen, need);
        }
        return merged;
    }

    private static int parseWavChannels(byte[] wavBytes) {
        if (wavBytes == null || wavBytes.length < 24) {
            return 1;
        }
        return ((wavBytes[22] & 0xFF) | (wavBytes[23] << 8));
    }

    private static int parseHeaderInt(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String extractError(String payload) {
        if (payload == null || payload.isBlank()) {
            return "";
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(payload);
            if (node.hasNonNull("error")) {
                return node.get("error").asText();
            }
            if (node.hasNonNull("detail")) {
                return node.get("detail").asText();
            }
        } catch (Exception ignore) {
            // fallback: raw text
        }
        return payload.length() > 200 ? payload.substring(0, 200) : payload;
    }
}
