package com.xiaozhi.dialogue.playback;

import com.xiaozhi.common.Speech;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.SentenceHelper;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.dialogue.llm.tool.mcp.device.DeviceMcpService;
import com.xiaozhi.utils.AudioUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiaozhi.common.utils.LatencyTracer;

/**
 * 语音合成器，用于非流式TTS（先生成完整音频文件再播放）。
 * 适用于不支持流式输出的TTS Provider（如 SherpaOnnx）。
 *
 * 数据流：LLM token流 → SentenceHelper分句 → 逐句调用TTS生成完整音频文件 → 读取PCM → 交给播放器播放
 */
@Slf4j
public class FileSynthesizer extends Synthesizer {

    private static final Pattern THINK_TAG_PATTERN =
            Pattern.compile("(?is)\\s*<think>.*?</think>\\s*");

    private static final Pattern TOOL_MARKER_ARG_PATTERN =
            Pattern.compile("^(.+?)\\s*[{].*[}]$", Pattern.DOTALL);

    private volatile Disposable llmDisposable;

    @Resource
    private DeviceMcpService deviceMcpService;

    public FileSynthesizer(ChatSession session, TtsService ttsService, Player player) {
        super(session, ttsService, player);
    }

    @Override
    public void cancel() {
        if (llmDisposable != null && !llmDisposable.isDisposed()) {
            llmDisposable.dispose();
        }
    }

    @Override
    public boolean isActive() {
        return llmDisposable != null && !llmDisposable.isDisposed();
    }

    @Override
    public void synthesize(Flux<String> stringFlux) {
        LatencyTracer.start(chatSession.getSessionId(), "TTS_FIRST_CHUNK");

        SentenceHelper sentenceHelper = new SentenceHelper();
        sentenceHelper.setToolMarkerCallback((toolName, arguments) -> {
            handleToolMarker(toolName, arguments);
        });

        llmDisposable = sentenceHelper.convert(stringFlux).subscribe(result -> {
            String rawText = result.text();
            String mood = result.mood();
            if (rawText != null) {
                rawText = THINK_TAG_PATTERN.matcher(rawText).replaceAll("").trim();
            }
            if (rawText == null || rawText.isEmpty()) {
                log.debug("句子在清洗标签后为空，跳过TTS - SessionId: {}", chatSession.getSessionId());
                return;
            }
            final String text = rawText;
            Flux<Speech> lazyTtsFlux = Flux.create(sink -> {
                try {
                    Path audioPath = ttsService.textToSpeech(text);
                    if (audioPath != null) {
                        List<byte[]> chunks = AudioUtils.readAsPcmChunks(audioPath.toString());
                        boolean first = true;
                        for (byte[] chunk : chunks) {
                            sink.next(first ? new Speech(chunk, text).withMood(mood) : new Speech(chunk));
                            first = false;
                        }
                        LatencyTracer.mark(chatSession.getSessionId(), "TTS_FIRST_CHUNK");
                    } else {
                        log.error("TTS服务返回空音频文件 - SessionId: {}", chatSession.getSessionId());
                    }
                } catch (Exception e) {
                    log.error("TTS合成出错: {} - SessionId: {}", e.getMessage(), chatSession.getSessionId());
                }
                sink.complete();
            });
            player.play(lazyTtsFlux);
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

            String result = callDeviceMcpTool(deviceId, toolName, argsMap);
            chatSession.addToolCallDetail(toolName, toolArgs, result);
            log.info("[TOOL_MARKER] 工具执行成功 - tool={}, result={}, sessionId={}",
                    toolName, result, chatSession.getSessionId());

        } catch (Exception e) {
            log.error("[TOOL_MARKER] 工具执行失败 - tool={}, error={}, sessionId={}",
                    toolName, e.getMessage(), chatSession.getSessionId());
            chatSession.addToolCallDetail(toolName, toolArgs, "工具执行失败: " + e.getMessage());
        }
    }

    private String callDeviceMcpTool(String deviceId, String toolName, Map<String, Object> args) {
        try {
            Map<String, Object> resultMap = deviceMcpService.callDeviceTool(deviceId, toolName, args);
            return resultMap != null ? resultMap.toString() : "null";
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("设备离线")) {
                throw e;
            }
            return "工具调用异常: " + e.getMessage();
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
            log.debug("文本在清洗think标签后为空，跳过TTS - SessionId: {}", chatSession.getSessionId());
            return;
        }
        synthesize(Flux.just(text));
    }

}
