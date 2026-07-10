package com.xiaozhi.communication.server.websocket;

import com.xiaozhi.communication.common.ChatSession;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketSession extends ChatSession {
    /**
     * 当前会话的链接 session
     */
    protected org.springframework.web.socket.WebSocketSession session;
    /**
     * Spring WebSocketSession 在并发 sendMessage 场景下并不总是线程安全；
     * 串行化文本/二进制发送，避免 tts 控制消息与音频帧并发导致丢消息。
     */
    private final Object sendLock = new Object();
    /**
     * 会话级下行序列号：统一为 text/binary 打序，便于排查乱序、丢包、插队。
     */
    private final AtomicLong outboundSeq = new AtomicLong(0);

    public WebSocketSession(String sessionId) {
        super(sessionId);
    }

    public WebSocketSession(org.springframework.web.socket.WebSocketSession session) {
        super(session.getId());
        this.session = session;
    }

    @Override
    public String getSessionId() {
        return session.getId();
    }

    public org.springframework.web.socket.WebSocketSession getSession() {
        return this.session;
    }

    @Override
    public void close() {
        if(session != null){
            try {
                session.close();
            } catch (IOException e) {
                log.error("关闭WebSocket会话时发生错误 - SessionId: {}", getSessionId(), e);
            }
        }
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public boolean isAudioChannelOpen() {
        return session.isOpen();
    }

    @Override
    public void sendTextMessage(String message) {
        synchronized (sendLock) {
            try {
                long seq = outboundSeq.incrementAndGet();
                if (log.isDebugEnabled()) {
                    String preview = message == null ? "null"
                            : (message.length() > 200 ? message.substring(0, 200) + "..." : message);
                    log.debug("WS OUT TEXT seq={} sessionId={} size={} payload={}",
                            seq, getSessionId(), message == null ? 0 : message.length(), preview);
                }
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("发送Text消息失败, message: {}", message, e);
            }
        }
    }

    @Override
    public void sendBinaryMessage(byte[] message) {
        synchronized (sendLock) {
            try {
                long seq = outboundSeq.incrementAndGet();
                if (log.isDebugEnabled()) {
                    log.debug("WS OUT BIN  seq={} sessionId={} size={}",
                            seq, getSessionId(), message == null ? 0 : message.length);
                }
                session.sendMessage(new BinaryMessage(message));
            } catch (Exception e) {
                log.error("发送Binary消息失败", e);
            }
        }
    }
}
