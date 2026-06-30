package com.xiaozhi.dialogue.playback;

import com.xiaozhi.common.Speech;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.SentenceHelper;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.utils.AudioUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import com.xiaozhi.common.utils.LatencyTracer;

import lombok.extern.slf4j.Slf4j;
/**
 * 语音合成器，用于非流式TTS（先生成完整音频文件再播放）。
 * 适用于不支持流式输出的TTS Provider（如 SherpaOnnx）。
 *
 * 数据流：LLM token流 → SentenceHelper分句 → 逐句调用TTS生成完整音频文件 → 读取PCM → 交给播放器播放
 */
@Slf4j
public class FileSynthesizer extends Synthesizer {

    /**
     * 思考标签正则：用于在分句后、送 TTS 之前的兜底清洗。
     * 覆盖 Persona.convert() 已过滤的场景之外的边界情况（如标签变体、
     * 部分模型在工具调用或系统提示中内联 <think> 等）。
     * DOTALL + 不区分大小写 + 含首尾空白，避免 TTS 把"让我想想..."念出来。
     */
    private static final Pattern THINK_TAG_PATTERN =
            Pattern.compile("(?is)\\s*<think>.*?</think>\\s*");

    // 保存LLM输出流的订阅引用，以便在cancel时取消上游订阅
    private volatile Disposable llmDisposable;

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

    /**
     * 将LLM输出的token流转化为语音并推送到播放器。
     * 使用 SentenceHelper 按标点分句，逐句调用TTS生成完整音频文件后交给播放器。
     *
     * @param stringFlux LLM输出的token流
     */
    @Override
    public void synthesize(Flux<String> stringFlux) {
        llmDisposable = new SentenceHelper().convert(stringFlux).subscribe(result -> {
            String rawText = result.text();
            String mood = result.mood();
            // TTS 兜底：去除 <think>...</think> 标签，避免把思考过程念成语音
            if (rawText != null) {
                rawText = THINK_TAG_PATTERN.matcher(rawText).replaceAll("").trim();
            }
            if (rawText == null || rawText.isEmpty()) {
                log.debug("句子在清洗 think 标签后为空，跳过 TTS - SessionId: {}", chatSession.getSessionId());
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

    /**
     * 直接合成单个文本
     * @param text 待合成的文本
     */
    @Override
    public void synthesize(String text) {
        // TTS 兜底：去除 <think>...</think> 标签（针对非流式路径）
        if (text != null) {
            text = THINK_TAG_PATTERN.matcher(text).replaceAll("").trim();
        }
        if (text == null || text.isEmpty()) {
            log.debug("文本在清洗 think 标签后为空，跳过 TTS - SessionId: {}", chatSession.getSessionId());
            return;
        }
        // 委托给 synthesize(Flux) 处理，缓存指标在那里统一记录
        synthesize(Flux.just(text));
    }

}
