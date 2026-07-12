package com.xiaozhi.dialogue.playback;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.TtsService;

/**
 * Synthesizer 工厂，创建对应的 Synthesizer 实现。
 *
 * <p>自动选择策略:
 * <ul>
 *   <li>moss-tts-nano → StreamSynthesizer(流式,首包延迟低)</li>
 *   <li>其它 provider → FileSynthesizer(通用,先生成完整音频再播放)</li>
 * </ul>
 *
 * <p>将来添加新的支持流式的 Provider,只需在此扩展判断。
 */
public class SynthesizerFactory {

    public static Synthesizer create(ChatSession session, TtsService ttsService, Player player) {
        String provider = ttsService.getProviderName();
        if ("moss-tts-nano".equals(provider)) {
            return new StreamSynthesizer(session, ttsService, player);
        }
        return new FileSynthesizer(session, ttsService, player);
    }
}
