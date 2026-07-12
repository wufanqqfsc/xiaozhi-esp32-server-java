package com.xiaozhi.communication.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.event.TtsPlaybackCompletedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSender {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ApplicationEventPublisher eventPublisher;

    public MessageSender(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendTtsMessage(ChatSession session, String text, String state) {
        if (session == null || !session.isOpen()) {
            log.error("ChatSession为null 或者已关闭，请检查！{}", Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "tts");
        messageJson.put("state", state);
        if (text != null) {
            messageJson.put("text", text);
        }

        String jsonMessage = messageJson.toString();
        log.info("sendTtsMessage发送消息 - SessionId: {}, Message: {}", session.getSessionId(), jsonMessage);
        sendTextMessage(session, jsonMessage);

        if ("stop".equals(state)) {
            eventPublisher.publishEvent(new TtsPlaybackCompletedEvent(this, session.getSessionId()));
        }
    }

    public void sendSttMessage(ChatSession session, String text) {
        if (session == null || !session.isOpen()) {
            log.warn("sendSttMessage无法发送消息 - 会话已关闭或为null");
            return;
        }
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("type", "stt");
        messageJson.put("text", text);

        String jsonMessage = messageJson.toString();
        log.info("sendSttMessage发送消息 - SessionId: {}, Message: {}", session.getSessionId(), jsonMessage);
        sendTextMessage(session, jsonMessage);
    }

    public void sendIotCommandMessage(ChatSession session, List<Map<String, Object>> commands) {
        if (session == null || !session.isOpen()) {
            log.warn("sendIotCommandMessage无法发送消息 - 会话已关闭或为null");
            return;
        }
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("session_id", session.getSessionId());
        messageJson.put("type", "iot");
        messageJson.set("commands", objectMapper.valueToTree(commands));

        String jsonMessage = messageJson.toString();
        log.debug("sendIotCommandMessage发送iot消息 - SessionId: {}, Message: {}", session.getSessionId(), messageJson);
        sendTextMessage(session, jsonMessage);
    }

    public void sendEmotion(ChatSession session, String emotion) {
        if (session == null || !session.isOpen()) {
            log.warn("sendEmotion无法发送消息 - 会话已关闭或为null");
            return;
        }
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("session_id", session.getSessionId());
        messageJson.put("type", "llm");
        messageJson.put("emotion", emotion);
        messageJson.put("text", emotion);
        String jsonMessage = messageJson.toString();
        log.info("sendEmotion发送Emotion消息 - SessionId: {}, Message: {}", session.getSessionId(), jsonMessage);
        sendTextMessage(session, jsonMessage);
    }

    public void sendTextMessage(ChatSession chatSession, String message) {
        try {
            if (chatSession == null || !chatSession.isOpen()) {
                log.warn("sendTextMessage无法发送消息 - SessionId: {}, isOpen: {}", chatSession != null ? chatSession.getSessionId() : "null", chatSession != null ? chatSession.isOpen() : false);
                return;
            }
            chatSession.sendTextMessage(message);
        } catch (Exception e) {
            log.error("发送消息时发生异常 - SessionId: {}, Error: {}", chatSession.getSessionId(), e.getMessage());
            throw new RuntimeException("发送消息失败, 消息内容: " + message, e);
        }
    }

    /**
     * 向设备发送 keepalive 帧,用于在 device 长时间无数据收到时
     * (例如 listen 阶段用户未说话)避免 device 端 IsTimeout 误判通道关闭。
     * ESP32 端 websocket_protocol 在收到任何 JSON 后会刷新 last_incoming_time_。
     */
    public void sendKeepalive(ChatSession chatSession) {
        if (chatSession == null || !chatSession.isOpen()) {
            return;
        }
        ObjectNode messageJson = objectMapper.createObjectNode();
        messageJson.put("session_id", chatSession.getSessionId());
        messageJson.put("type", "keepalive");
        String jsonMessage = messageJson.toString();
        if (log.isDebugEnabled()) {
            log.debug("sendKeepalive - SessionId: {}", chatSession.getSessionId());
        }
        sendTextMessage(chatSession, jsonMessage);
    }

    public void sendBinaryMessage(ChatSession chatSession, byte[] opusFrame) {
        try {
            if (chatSession == null || !chatSession.isOpen()) {
                log.warn("发送Opus帧失败，session未打开 - SessionId: {}, isOpen: {}", chatSession != null ? chatSession.getSessionId() : "null", chatSession != null ? chatSession.isOpen() : false);
                return;
            }
            chatSession.sendBinaryMessage(opusFrame);
        } catch (Exception e) {
            log.error("发送消息时发生异常 - SessionId: {}, Error: {}", chatSession.getSessionId(), e.getMessage());
            throw new RuntimeException("发送音频消息失败, 消息内容", e);
        }
    }
}
