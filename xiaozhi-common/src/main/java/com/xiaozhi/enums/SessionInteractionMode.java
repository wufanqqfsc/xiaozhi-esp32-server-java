package com.xiaozhi.enums;

/**
 * 会话交互模式（T13）：区分 JARVIS 菜单、占卜跑马灯、摇一摇等链路，供 Dialogue / TTS 编排使用。
 */
public enum SessionInteractionMode {
    /** 默认对话 */
    IDLE,
    /** 唤醒后展示 JARVIS 菜单 */
    JARVIS_MENU,
    /** 用户已选菜单项，等待 start_divination */
    DIVINATION_PENDING,
    /** 设备端跑马灯进行中（已调用 start_divination 或摇一摇已触发） */
    DIVINATION_ACTIVE,
    /** 链路 A：摇一摇触发的占卜 */
    SHAKE_DIVINATION
}
