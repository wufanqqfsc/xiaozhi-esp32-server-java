package com.xiaozhi.dialogue.playback;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.ai.tts.TtsService;
import com.xiaozhi.dialogue.llm.tool.mcp.device.DeviceMcpService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Synthesizer 工厂，创建对应的 Synthesizer 实现。
 *
 * <p>自动选择策略:
 * <ul>
 *   <li>moss-tts-nano → StreamSynthesizer(流式,首包延迟低)</li>
 *   <li>其它 provider → FileSynthesizer(通用,先生成完整音频再播放)</li>
 * </ul>
 */
@Component
public class SynthesizerFactory {

    @Resource
    private DeviceMcpService deviceMcpService;

    public Synthesizer create(ChatSession session, TtsService ttsService, Player player) {
        String provider = ttsService.getProviderName();
        if ("moss-tts-nano".equals(provider)) {
            return new StreamSynthesizer(session, ttsService, player, deviceMcpService);
        }
        return new FileSynthesizer(session, ttsService, player, deviceMcpService);
    }
}
