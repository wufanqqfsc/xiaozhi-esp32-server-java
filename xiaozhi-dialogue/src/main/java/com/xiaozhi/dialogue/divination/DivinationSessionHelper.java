package com.xiaozhi.dialogue.divination;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.dialogue.llm.tool.mcp.device.DeviceMcpService;
import com.xiaozhi.dialogue.playback.Synthesizer;
import com.xiaozhi.enums.SessionInteractionMode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 摇一摇 / JARVIS 占卜链路的会话状态与 TTS 收尾辅助（T13/T14/T17/T18）。
 */
@Slf4j
public final class DivinationSessionHelper {

    public static final String CLOSING_PHRASE = "祝先生今日顺遂。";
    public static final String SHAKE_PROMPT_MARKER = "[设备摇一摇事件]";
    public static final String GET_DIVINATION_RESULT_TOOL = "self.attitude.get_divination_result";

    /** T18: LLM 未调工具直接播报占卜内容时的特征文本 */
    private static final Pattern DIVINATION_RESULT_HINT = Pattern.compile(
            "占卜结果|本次罗盘占卜|综合运势|一、综合运势");

    private DivinationSessionHelper() {
    }

    public static boolean isShakeDevicePrompt(String text) {
        return text != null && text.contains(SHAKE_PROMPT_MARKER);
    }

    public static boolean isDivinationTtsFlow(SessionInteractionMode mode) {
        return mode == SessionInteractionMode.DIVINATION_ACTIVE
                || mode == SessionInteractionMode.SHAKE_DIVINATION;
    }

    public static boolean isDivinationFlow(SessionInteractionMode mode) {
        return isDivinationTtsFlow(mode) || mode == SessionInteractionMode.DIVINATION_PENDING;
    }

    public static void onWakeWord(ChatSession session) {
        session.setInteractionMode(SessionInteractionMode.JARVIS_MENU);
        session.setDivinationResultFetched(false);
    }

    public static void onMenuSelection(ChatSession session) {
        session.setInteractionMode(SessionInteractionMode.DIVINATION_PENDING);
        session.setDivinationResultFetched(false);
    }

    public static void onShakePrompt(ChatSession session) {
        session.setInteractionMode(SessionInteractionMode.SHAKE_DIVINATION);
        session.setDivinationResultFetched(false);
    }

    public static void onStartDivinationTool(ChatSession session) {
        session.setInteractionMode(SessionInteractionMode.DIVINATION_ACTIVE);
        session.setDivinationResultFetched(false);
    }

    public static void clearDivinationFlow(ChatSession session) {
        session.setInteractionMode(SessionInteractionMode.IDLE);
        session.setDivinationResultFetched(false);
    }

    public static void onToolInvoked(ChatSession session, String toolName) {
        if (toolName == null) {
            return;
        }
        if (toolName.contains("start_divination")) {
            onStartDivinationTool(session);
        }
        if (toolName.contains("get_divination_result")) {
            session.setDivinationResultFetched(true);
        }
    }

    /**
     * T17: 跑马灯期间用户继续说话时注入的 LLM 提示。
     */
    public static String buildMarqueeBusyHint() {
        return "[系统提示] 占卜跑马灯仍在进行中。请仅回复："
                + "「先生，罗盘正在为您占卜，请稍候片刻。」不要调用其它工具。";
    }

    /**
     * T18: LLM 跳过 get_divination_result 直接输出运势时，强制补调设备 MCP 工具。
     */
    public static void maybeForceGetDivinationResult(
            ChatSession session,
            String text,
            DeviceMcpService deviceMcpService) {
        if (session == null || text == null || session.isDivinationResultFetched()) {
            return;
        }
        if (!isDivinationFlow(session.getInteractionMode())) {
            return;
        }
        if (!DIVINATION_RESULT_HINT.matcher(text).find()) {
            return;
        }
        if (deviceMcpService == null || session.getDevice() == null) {
            log.warn("[T18] 无法强制 get_divination_result：DeviceMcpService 或设备为空");
            return;
        }
        String deviceId = session.getDevice().getDeviceId().replace(":", "-");
        try {
            log.warn("[T18] LLM 未调用 get_divination_result，强制补调 - sessionId={}, textHead={}",
                    session.getSessionId(),
                    text.length() > 40 ? text.substring(0, 40) + "..." : text);
            Map<String, Object> result = deviceMcpService.callDeviceTool(
                    deviceId, GET_DIVINATION_RESULT_TOOL, Map.of());
            String resultStr = result != null ? result.toString() : "null";
            session.addToolCallDetail(GET_DIVINATION_RESULT_TOOL, "", resultStr);
            onToolInvoked(session, GET_DIVINATION_RESULT_TOOL);
        } catch (Exception e) {
            log.error("[T18] 强制 get_divination_result 失败 - sessionId={}, error={}",
                    session.getSessionId(), e.getMessage());
        }
    }

    /**
     * T14: 占卜解读 TTS 流结束后补播结束语（若 LLM 未包含）。
     */
    public static void maybeAppendDivinationClosing(Synthesizer synthesizer, ChatSession session) {
        if (synthesizer == null || session == null) {
            return;
        }
        SessionInteractionMode mode = session.getInteractionMode();
        if (!isDivinationTtsFlow(mode)) {
            return;
        }
        session.setInteractionMode(SessionInteractionMode.JARVIS_MENU);
        session.setDivinationResultFetched(false);
        synthesizer.synthesize(CLOSING_PHRASE);
    }
}
