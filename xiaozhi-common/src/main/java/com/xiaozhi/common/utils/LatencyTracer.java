package com.xiaozhi.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 延迟埋点工具，用于追踪语音交互链路各阶段的耗时。
 *
 * 设计：
 *   - 通过 ThreadLocal<TraceSession> 在 WebSocket 入口设置 sessionId
 *   - 各阶段（STT/LLM/TTS/Player）调用 mark(stage) 时自动从 ThreadLocal 取 sessionId
 *   - 也可显式传入 sessionId 用于跨线程场景（Reactor/虚拟线程）
 *
 * 输出格式：
 *   [LATENCY][sessionId] stage delta=Xms total=Yms
 *
 * 阶段标签建议：
 *   STT_RECV / STT_DONE
 *   LLM_FIRST_TOKEN / LLM_DONE
 *   TTS_SENT_FIRST / TTS_SENT_DONE
 *   FIRST_AUDIO_SENT / ALL_AUDIO_SENT
 */
@Slf4j
public final class LatencyTracer {

    private static final Map<String, Long> START_TIMES = new ConcurrentHashMap<>();
    private static final Map<String, Long> LAST_TIMES = new ConcurrentHashMap<>();

    private static final ThreadLocal<String> CURRENT_SESSION = new ThreadLocal<>();
    private static final AtomicLong SESSION_COUNTER = new AtomicLong(0);

    private static final boolean ENABLED = true;

    private LatencyTracer() {}

    /**
     * 在当前线程上设置会话ID，供后续 mark() 使用
     */
    public static void setSession(String sessionId) {
        if (!ENABLED) return;
        CURRENT_SESSION.set(sessionId);
    }

    /**
     * 获取当前线程上的会话ID（无则自动生成一个匿名ID）
     */
    public static String currentSession() {
        String s = CURRENT_SESSION.get();
        if (s == null) {
            s = "anon-" + SESSION_COUNTER.incrementAndGet();
            CURRENT_SESSION.set(s);
        }
        return s;
    }

    /**
     * 清除当前线程上的会话上下文
     */
    public static void clearSession() {
        CURRENT_SESSION.remove();
    }

    /**
     * 启动一次会话追踪，记录起点时间
     */
    public static void start(String sessionId, String stage) {
        if (!ENABLED) return;
        long now = System.nanoTime();
        String key = sessionId + ":" + stage;
        START_TIMES.put(key, now);
        LAST_TIMES.put(key, now);
        log.info("[LATENCY][{}] {} START total=0ms", sessionId, stage);
    }

    /**
     * 标记一个阶段完成（自动从 ThreadLocal 取 sessionId）
     */
    public static void mark(String stage) {
        mark(currentSession(), stage);
    }

    /**
     * 标记一个阶段完成（显式 sessionId）
     */
    public static void mark(String sessionId, String stage) {
        if (!ENABLED) return;
        long now = System.nanoTime();
        String key = sessionId + ":" + stage;
        Long startNs = START_TIMES.get(key);
        Long lastNs = LAST_TIMES.get(key);
        if (startNs == null) {
            log.info("[LATENCY][{}] {} ORPHAN (no start)", sessionId, stage);
            return;
        }
        long totalMs = (now - startNs) / 1_000_000L;
        long deltaMs = (lastNs != null) ? (now - lastNs) / 1_000_000L : totalMs;
        LAST_TIMES.put(key, now);
        log.info("[LATENCY][{}] {} delta={}ms total={}ms", sessionId, stage, deltaMs, totalMs);
    }

    /**
     * 清理一次会话的所有追踪数据
     */
    public static void clear(String sessionId) {
        if (!ENABLED) return;
        START_TIMES.keySet().removeIf(k -> k.startsWith(sessionId + ":"));
        LAST_TIMES.keySet().removeIf(k -> k.startsWith(sessionId + ":"));
    }

    /**
     * 仅打点（不依赖 start）
     */
    public static void instant(String stage) {
        if (!ENABLED) return;
        log.info("[LATENCY][{}] {} t={}ms", currentSession(), stage, System.currentTimeMillis() % 1_000_000);
    }
}
