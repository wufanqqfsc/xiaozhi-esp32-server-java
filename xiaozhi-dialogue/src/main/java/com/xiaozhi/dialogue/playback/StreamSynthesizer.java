package com.xiaozhi.dialogue.playback;

import com.xiaozhi.ai.tts.SentenceHelper;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.common.Speech;
import com.xiaozhi.common.utils.LatencyTracer;
import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.providers.MossTtsNanoTtsService;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

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

    /** 当前活跃的 LLM 流订阅;用于 cancel() 终止上游 */
    private volatile Disposable llmDisposable;

    /** 当前是否仍有活跃播放/合成(供外部打断判断) */
    private final AtomicBoolean active = new AtomicBoolean(false);

    public StreamSynthesizer(ChatSession session, TtsService ttsService, Player player) {
        super(session, ttsService, player);
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

        llmDisposable = new SentenceHelper().convert(stringFlux).subscribe(sentence -> {
            String rawText = sentence.text();
            String mood = sentence.mood();
            if (rawText != null) {
                rawText = THINK_TAG_PATTERN.matcher(rawText).replaceAll("").trim();
            }
            if (rawText == null || rawText.isEmpty()) {
                log.debug("句子在清洗 think 标签后为空,跳过 TTS - SessionId: {}", chatSession.getSessionId());
                return;
            }
            final String text = rawText;
            active.set(true);
            try {
                Flux<byte[]> pcmFlux = moss.streamAsFlux(text);
                // 累计首个 PCM chunk 到达即可打点
                AtomicBoolean firstChunkMarked = new AtomicBoolean(false);
                Flux<Speech> speechFlux = pcmFlux
                        .mapNotNull(chunk -> (chunk == null || chunk.length == 0) ? null : chunk)
                        .map(chunk -> {
                            if (firstChunkMarked.compareAndSet(false, true)) {
                                LatencyTracer.mark(chatSession.getSessionId(), "TTS_FIRST_CHUNK");
                            }
                            Speech sp = new Speech(chunk);
                            // 仅第一个 chunk 携带 text/mood
                            if (chunk == sp.getOutput()) {
                                // 标记但实际由下游处理;此处仍只把第一帧附加 text/mood
                            }
                            return sp;
                        });

                // 用 transform 给首个 Speech 加 text/mood
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
        });
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
