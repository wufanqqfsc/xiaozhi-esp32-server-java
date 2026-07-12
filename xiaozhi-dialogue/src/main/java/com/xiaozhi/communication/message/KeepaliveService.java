package com.xiaozhi.communication.message;

import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.communication.common.SessionManager;
import com.xiaozhi.enums.DeviceState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 通道 keepalive 服务
 *
 * 在 device 进入 LISTENING 状态后,server 端会持续收到 device 发来的 listen 帧和 audio,
 * 但 server 在用户未开口前不会主动向 device 推消息。
 * device 端 websocket_protocol 的 IsTimeout 仅在收到 JSON/audio 后才刷新时间戳,
 * 因此 listen 阶段超过 120s(修复后 300s)会被误判为通道超时而断开。
 *
 * 本服务在 LISTENING 状态期间每 30s 向 device 推一帧
 * {"type":"keepalive"} JSON,触发 device 刷新 last_incoming_time_,
 * 避免误判通道超时(根因:ESP32 第二轮/多轮对话 162s 通道超时问题)。
 */
@Slf4j
@Service
public class KeepaliveService {

    /** keepalive 间隔(ms):30s,在 ESP32 端 300s 阈值下能稳定保持活跃 */
    private static final long KEEPALIVE_INTERVAL_MS = 30_000L;

    private final SessionManager sessionManager;
    private final MessageSender messageSender;
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final Map<String, java.util.function.BiConsumer<DeviceState, DeviceState>> sessionListeners =
            new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    public KeepaliveService(SessionManager sessionManager, MessageSender messageSender) {
        this.sessionManager = sessionManager;
        this.messageSender = messageSender;
    }

    @PostConstruct
    public void start() {
        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "ws-keepalive");
            t.setDaemon(true);
            return t;
        });
        log.info("KeepaliveService started, interval={}ms", KEEPALIVE_INTERVAL_MS);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        tasks.values().forEach(f -> f.cancel(false));
        tasks.clear();
    }

    /**
     * 新会话打开事件:为该会话注册状态监听器,
     * 进入 LISTENING 时启动 keepalive 定时器,离开时停止。
     */
    @EventListener
    public void onSessionOpened(com.xiaozhi.event.ChatSessionOpenedEvent event) {
        ChatSession session = sessionManager.getSession(event.getSessionId());
        if (session == null) return;
        String sid = event.getSessionId();
        java.util.function.BiConsumer<DeviceState, DeviceState> listener = (oldS, newS) -> {
            if (newS == DeviceState.LISTENING) {
                startKeepalive(sid);
            } else {
                stopKeepalive(sid);
            }
        };
        session.addStateListener(listener);
        sessionListeners.put(sid, listener);
    }

    /**
     * 会话关闭事件:停止 keepalive,注销监听器,避免内存泄漏。
     */
    @EventListener
    public void onSessionClosed(com.xiaozhi.event.ChatSessionClosedEvent event) {
        String sid = event.getSessionId();
        stopKeepalive(sid);
        ChatSession session = sessionManager.getSession(sid);
        java.util.function.BiConsumer<DeviceState, DeviceState> listener = sessionListeners.remove(sid);
        if (session != null && listener != null) {
            session.removeStateListener(listener);
        }
    }

    private void startKeepalive(String sessionId) {
        if (scheduler == null) return;
        if (tasks.containsKey(sessionId)) return;
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                ChatSession session = sessionManager.getSession(sessionId);
                if (session == null || !session.isOpen()) {
                    stopKeepalive(sessionId);
                    return;
                }
                if (session.getDeviceState() != DeviceState.LISTENING) {
                    stopKeepalive(sessionId);
                    return;
                }
                messageSender.sendKeepalive(session);
            } catch (Exception e) {
                log.warn("keepalive failed for session {}: {}", sessionId, e.getMessage());
            }
        }, KEEPALIVE_INTERVAL_MS, KEEPALIVE_INTERVAL_MS, TimeUnit.MILLISECONDS);
        tasks.put(sessionId, future);
        log.info("Keepalive started - SessionId: {}", sessionId);
    }

    private void stopKeepalive(String sessionId) {
        ScheduledFuture<?> f = tasks.remove(sessionId);
        if (f != null) {
            f.cancel(false);
            log.info("Keepalive stopped - SessionId: {}", sessionId);
        }
    }
}