package com.xiaozhi.dialogue.playback;

import com.xiaozhi.ai.tts.SentenceHelper;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.common.Speech;
import com.xiaozhi.common.utils.LatencyTracer;
import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.providers.MossTtsNanoTtsService;
import com.xiaozhi.dialogue.divination.DivinationSessionHelper;
import com.xiaozhi.dialogue.llm.tool.mcp.device.DeviceMcpService;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 流式语音合成器,配合支持流式输出的 TTS Provider(MOSS-TTS-Nano)。
 *
 * <p>数据流:
 * <pre>
 *   LLM token 流 → SentenceHelper 分句 → 逐句流式 TTS → 实时下发 Player
 * </pre>
 *
 * <p>与 {@link FileSynthesizer} 的核心区别:
 * 本合成器在每个 TTS 句子生成过程中,一边接收 PCM chunk 一边推送到 Player,
 * 首包延迟显著降低(从整句生成时间降至首帧生成时间 ~300-500ms)。
 *
 * <p>注:StreamSynthesizer 仅在 moss-tts-nano provider 下使用,见 SynthesizerFactory。
 */
@Slf4j
public class StreamSynthesizer extends Synthesizer {

    private static final Pattern THINK_TAG_PATTERN =
            Pattern.compile("(?is)\\s*<think>.*?</think>\\s*");

    private static final Pattern TOOL_MARKER_ARG_PATTERN =
            Pattern.compile("^(.+?)\\s*[{].*[}]$", Pattern.DOTALL);

    /** 当前活跃的 LLM 流订阅;用于 cancel() 终止上游 */
    private volatile Disposable llmDisposable;

    /** 当前是否仍有活跃播放/合成(供外部打断判断) */
    private final AtomicBoolean active = new AtomicBoolean(false);

    private final DeviceMcpService deviceMcpService;

    public StreamSynthesizer(ChatSession session, TtsService ttsService, Player player,
                             DeviceMcpService deviceMcpService) {
        super(session, ttsService, player);
        this.deviceMcpService = deviceMcpService;
    }

    @Override
    public void cancel() {
        if (llmDisposable != null && !llmDisposable.isDisposed()) {
            llmDisposable.dispose();
        }
        if (ttsService instanceof MossTtsNanoTtsService moss) {
            moss.cancelInflightStream();
        }
        active.set(false);
    }

    @Override
    public boolean isActive() {
        if (llmDisposable != null && !llmDisposable.isDisposed()) {
            return true;
        }
        return active.get();
    }

    @Override
    public void synthesize(Flux<String> stringFlux) {
        LatencyTracer.start(chatSession.getSessionId(), "TTS_FIRST_CHUNK");

        if (!(ttsService instanceof MossTtsNanoTtsService moss)) {
            log.error("StreamSynthesizer 仅支持 moss-tts-nano provider, 实际: {} - SessionId: {}",
                    ttsService.getProviderName(), chatSession.getSessionId());
            return;
        }

        SentenceHelper sentenceHelper = new SentenceHelper();
        sentenceHelper.setToolMarkerCallback((toolName, arguments) ->
                handleToolMarker(toolName, arguments));

        llmDisposable = sentenceHelper.convert(stringFlux).subscribe(sentence -> {
            String rawText = sentence.text();
            String mood = sentence.mood();
            if (rawText != null) {
                rawText = THINK_TAG_PATTERN.matcher(rawText).replaceAll("").trim();
            }
            if (rawText == null || rawText.isEmpty()) {
                log.debug("句子在清洗 think 标签后为空,跳过 TTS - SessionId: {}", chatSession.getSessionId());
                return;
            }
            DivinationSessionHelper.maybeForceGetDivinationResult(chatSession, rawText, deviceMcpService);
            final String text = rawText;
            active.set(true);
            try {
                Flux<byte[]> pcmFlux = moss.streamAsFlux(text);
                AtomicBoolean firstChunkMarked = new AtomicBoolean(false);
                Flux<Speech> speechFlux = pcmFlux
                        .mapNotNull(chunk -> (chunk == null || chunk.length == 0) ? null : chunk)
                        .map(chunk -> {
                            if (firstChunkMarked.compareAndSet(false, true)) {
                                LatencyTracer.mark(chatSession.getSessionId(), "TTS_FIRST_CHUNK");
                            }
                            return new Speech(chunk);
                        });

                Flux<Speech> withMeta = speechFlux
                        .index()
                        .map(tuple -> {
                            long idx = tuple.getT1();
                            Speech sp = tuple.getT2();
                            if (idx == 0L) {
                                return new Speech(sp.getOutput(), text).withMood(mood);
                            }
                            return sp;
                        });

                player.play(withMeta);
            } catch (Exception e) {
                log.error("MOSS-TTS-Nano 流式合成出错: {} - SessionId: {}", e.getMessage(), chatSession.getSessionId());
            } finally {
                active.set(false);
            }
        }, error -> {
            log.error("LLM 流式订阅出错: {} - SessionId: {}", error.getMessage(), chatSession.getSessionId());
            active.set(false);
        }, () -> {
            active.set(false);
            DivinationSessionHelper.maybeAppendDivinationClosing(this, chatSession);
        });
    }

    private void handleToolMarker(String rawContent, String arguments) {
        String toolName = rawContent.trim();
        String toolArgs = "";
        Matcher m = TOOL_MARKER_ARG_PATTERN.matcher(toolName);
        if (m.matches()) {
            toolName = m.group(1).trim();
            toolArgs = m.group(2);
        }

        log.info("[TOOL_MARKER] 检测到工具调用标记 - tool={}, args={}, sessionId={}",
                toolName, toolArgs, chatSession.getSessionId());

        try {
            String deviceId = chatSession.getDevice() != null
                    ? chatSession.getDevice().getDeviceId().replace(":", "-")
                    : null;

            if (deviceId == null) {
                log.warn("[TOOL_MARKER] 设备未连接，无法执行工具: {}", toolName);
                chatSession.addToolCallDetail(toolName, toolArgs, "设备未连接");
                return;
            }

            Map<String, Object> argsMap = StringUtils.hasText(toolArgs)
                    ? parseJsonArgs(toolArgs)
                    : Map.of();

            Map<String, Object> resultMap = deviceMcpService.callDeviceTool(deviceId, toolName, argsMap);
            String result = resultMap != null ? resultMap.toString() : "null";
            chatSession.addToolCallDetail(toolName, toolArgs, result);
            DivinationSessionHelper.onToolInvoked(chatSession, toolName);
            log.info("[TOOL_MARKER] 工具执行成功 - tool={}, result={}, sessionId={}",
                    toolName, result, chatSession.getSessionId());

        } catch (Exception e) {
            log.error("[TOOL_MARKER] 工具执行失败 - tool={}, error={}, sessionId={}",
                    toolName, e.getMessage(), chatSession.getSessionId());
            chatSession.addToolCallDetail(toolName, toolArgs, "工具执行失败: " + e.getMessage());
        }
    }

    private Map<String, Object> parseJsonArgs(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.warn("[TOOL_MARKER] JSON参数解析失败: {}", json);
            return Map.of();
        }
    }

    @Override
    public void synthesize(String text) {
        if (text != null) {
            text = THINK_TAG_PATTERN.matcher(text).replaceAll("").trim();
        }
        if (text == null || text.isEmpty()) {
            log.debug("文本在清洗 think 标签后为空,跳过 TTS - SessionId: {}", chatSession.getSessionId());
            return;
        }
        synthesize(Flux.just(text));
    }
}
