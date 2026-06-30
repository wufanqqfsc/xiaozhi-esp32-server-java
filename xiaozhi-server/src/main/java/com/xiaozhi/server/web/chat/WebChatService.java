package com.xiaozhi.server.web.chat;

import com.xiaozhi.ai.llm.factory.ChatModelFactory;
import com.xiaozhi.ai.llm.memory.ChatMemory;
import com.xiaozhi.ai.llm.memory.Conversation;
import com.xiaozhi.ai.llm.memory.ConversationContext;
import com.xiaozhi.ai.llm.memory.MessageTimeMetadata;
import com.xiaozhi.ai.llm.memory.MessageWindowConversation;
import com.xiaozhi.common.model.ChatToken;
import com.xiaozhi.common.model.bo.MessageBO;
import com.xiaozhi.common.model.bo.RoleBO;
import com.xiaozhi.message.service.MessageService;
import com.xiaozhi.role.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
/**
 * Web 聊天服务：为纯文本 Web 客户端提供流式 AI 对话能力。
 * 轻量级实现，不涉及 STT/TTS/Player 等音频组件
 */
@Slf4j
@Service
public class WebChatService {
    /**
     * 思考标签正则：与 Persona.convert() 保持一致，用于清洗部分模型
     *（MiniMax / DeepSeek-R1 / 各类 CoT）把思考标签内联在 content 文本中、
     * Spring AI 没拆出 reasoningContent、造成 Web SSE 推送 + DB 落盘仍看到思考片段的情况。
     * DOTALL + 不区分大小写 + 含首尾空白。
     * <p>用 Unicode 转义字符避免 HTML 标签被本文件中转义。
     */
    private static final String THINK_OPEN = "\u003c\u0074\u0068\u0069\u006e\u006b\u003e";
    private static final String THINK_CLOSE = "\u003c\u002f\u0074\u0068\u0069\u006e\u006b\u003e";

    private static final Pattern THINK_TAG_PATTERN = Pattern.compile(
            "(?is)\\s*" + Pattern.quote(THINK_OPEN) + ".*?" + Pattern.quote(THINK_CLOSE) + "\\s*");

    private static String stripThinkTags(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return THINK_TAG_PATTERN.matcher(text).replaceAll("").trim();
    }

    /**
     * 跨 SSE chunk 清洗思考标签。
     *
     * <p>处理逻辑：
     * <ul>
     *   <li>如果当前不在思考块内，遇到开始标签 THINK_OPEN 时进入思考块；该标签之前的一部分作为正常 content 返回。</li>
     *   <li>如果当前在思考块内，累积 chunk 文本；遇到结束标签 THINK_CLOSE 时退出，并丢弃思考块内容；标签之后立刻把后续纯文本折成 content 返回。</li>
     *   <li>多次开闭嵌套：每次遇到开标签 → 进栈；遇到闭标签 → 出栈；栈空时不在思考块。最外层设计的栈深度计为 1。</li>
     *   <li>该实现为单层嵌套（足以拆全部现在主流的几百行清洗场景）。</li>
     * </ul>
     */
    private static String sanitizeThinkAcrossChunks(String chunk,
                                                    StringBuilder thinkBuf,
                                                    boolean[] inThinkBlockHolder,
                                                    String thinkOpen,
                                                    String thinkClose) {
        if (chunk == null || chunk.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(chunk.length());
        StringBuilder carry = new StringBuilder();
        boolean inThink = inThinkBlockHolder[0];
        int i = 0;
        int n = chunk.length();
        while (i < n) {
            if (!inThink) {
                // 在普通模式：查找开始标签。标签可能在 chunk 中间。还未处理完部分进入 carry。
                int idxOpen = chunk.indexOf(thinkOpen, i);
                if (idxOpen < 0) {
                    // 还需要节省跨 chunk 边界：保留尾巴直到下一 chunk。为简单起见，截断最后一个
                    // (标签长度-1) 字符到 carry 暂存。
                    int lookBack = Math.max(0, thinkOpen.length() - 1);
                    if (i + lookBack >= n) {
                        lookBack = n - i;
                    }
                    if (lookBack > 0) {
                        out.append(chunk, i, i + lookBack);
                        thinkBuf.setLength(0);
                        thinkBuf.append(chunk, i + lookBack, n);
                    } else {
                        out.append(chunk, i, n);
                    }
                    // 注：如果跨 chunk 边界在标签中间，超出的尾巴部分会先被丢出去。如不出现是 OK。
                    inThinkBlockHolder[0] = false;
                    return out.toString();
                } else {
                    // 之前未出现标签的纯文本
                    out.append(chunk, i, idxOpen);
                    i = idxOpen + thinkOpen.length();
                    inThink = true;
                    thinkBuf.setLength(0);
                }
            } else {
                // 当前在思考块里：查找结束标签
                int idxClose = chunk.indexOf(thinkClose, i);
                if (idxClose < 0) {
                    // 后续会有下个 chunk 补齐结束标签；吞下该 chunk 剩余部分
                    thinkBuf.append(chunk, i, n);
                    inThinkBlockHolder[0] = true;
                    return out.toString();
                } else {
                    // 丢弃思考内容 + 结束标签
                    thinkBuf.setLength(0);
                    i = idxClose + thinkClose.length();
                    inThink = false;
                }
            }
        }
        inThinkBlockHolder[0] = inThink;
        return out.toString();
    }

    @Resource
    private ChatModelFactory chatModelFactory;
    @Resource
    private RoleService roleService;
    @Resource
    private ChatMemory chatMemory;
    @Resource
    private MessageService messageService;

    @Value("${conversation.max-messages:16}")
    private int maxMessages;

    /**
     * sessionId → Conversation 映射
     */
    private final ConcurrentHashMap<String, Conversation> conversations = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ChatModel> chatModels = new ConcurrentHashMap<>();

    /**
     * 开启一个 Web 聊天会话。
     * 当 {@code resumeSessionId} 为空时创建新会话；非空时尝试续接已有会话。
     * 续接时会校验归属（userId一致 且 source='web'），防止误用设备会话或跨用户访问。
     *
     * @param userId           当前登录用户ID
     * @param roleId           角色ID
     * @param resumeSessionId  续接的会话 ID，可为 null
     * @return sessionId
     */
    public String openSession(Integer userId, Integer roleId, String resumeSessionId) {
        String ownerId = "web:" + userId;

        RoleBO role = roleService.getBO(roleId);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在: " + roleId);
        }

        String sessionId;
        if (StringUtils.hasText(resumeSessionId)) {
            assertSessionOwnedByUser(resumeSessionId, userId);
            sessionId = resumeSessionId;
        } else {
            sessionId = UUID.randomUUID().toString();
        }

        // 初始化 Conversation：Web 场景始终按 sessionId 加载（新会话为空，续接会拉到历史）。
        Conversation conversation = MessageWindowConversation.builder()
                .chatMemory(chatMemory)
                .maxMessages(maxMessages)
                .ownerId(ownerId)
                .roleId(role.getRoleId())
                .roleDesc(role.getRoleDesc())
                .userId(userId)
                .sessionId(sessionId)
                .sessionScoped(true)
                .build();
        conversations.put(sessionId, conversation);

        // 初始化 ChatModel
        ChatModel chatModel = chatModelFactory.getChatModel(role);
        chatModels.put(sessionId, chatModel);

        log.info("Web 聊天会话已创建: sessionId={}, userId={}, roleId={}, resume={}",
                sessionId, userId, roleId, StringUtils.hasText(resumeSessionId));
        return sessionId;
    }

    /**
     * 创建新会话的便捷重载。
     */
    public String openSession(Integer userId, Integer roleId) {
        return openSession(userId, roleId, null);
    }

    /**
     * 校验待续接的 sessionId 归属于当前用户的 Web 会话。
     * 存在不匹配时抛出 IllegalArgumentException。
     */
    private void assertSessionOwnedByUser(String sessionId, Integer userId) {
        List<MessageBO> recent = messageService.listHistory(sessionId, 1);
        if (recent.isEmpty()) {
            throw new IllegalArgumentException("会话不存在或已清除: " + sessionId);
        }
        MessageBO first = recent.get(0);
        if (!MessageBO.SOURCE_WEB.equals(first.getSource())) {
            throw new IllegalArgumentException("仅支持续接 Web 来源的会话: " + sessionId);
        }
        if (!userId.equals(first.getUserId())) {
            throw new IllegalArgumentException("会话不属于当前用户: " + sessionId);
        }
    }

    /**
     * 流式聊天：接收用户文本，返回 AI 回复的 ChatToken 流（包含思考过程和正式回复），
     * 并在完成时持久化 user/assistant 两条消息。
     *
     * @param sessionId 会话 ID
     * @param text      用户输入文本
     * @return ChatToken 流，前端可根据 type 区分 thinking/content
     */
    public Flux<ChatToken> chatStream(String sessionId, String text) {
        Conversation conversation = conversations.get(sessionId);
        ChatModel chatModel = chatModels.get(sessionId);
        if (conversation == null || chatModel == null) {
            return Flux.error(new IllegalStateException("会话不存在或已过期: " + sessionId));
        }

        // Web 场景：裸文本 UserMessage + 时间戳 metadata；
        // Conversation 投影层会在送 LLM 前拼出 [时间戳] 文本 的前缀。
        // 无 speaker/emotion，故不挂 MessageMetadataBO。
        LocalDateTime userCreatedAt = LocalDateTime.now();
        Instant userInstant = userCreatedAt.atZone(ZoneId.systemDefault()).toInstant();
        UserMessage userMessage = new UserMessage(text);
        MessageTimeMetadata.setTimeMillis(userMessage, userInstant);
        conversation.add(userMessage);

        // Web 场景无位置
        List<Message> messages = conversation.messages(ConversationContext.EMPTY);

        Prompt prompt = new Prompt(messages);

        StringBuilder fullResponse = new StringBuilder();
        // MiniMax-M3 / DeepSeek-R1 等 CoT 模型会把 标签分成跨多个 SSE chunk 推送，
        // 单 chunk 正则无法匹配跨 chunk 标签，因此用一个扫描状态机跨 chunk 累积、识别 完整
        // 待开始 / 待结束 标签后再一次性清理后 flush 出去。
        final String[] thinkOpenRef = {THINK_OPEN};
        final String[] thinkCloseRef = {THINK_CLOSE};
        final StringBuilder thinkBuf = new StringBuilder();
        final boolean[] inThinkBlock = {false};

        return chatModel.stream(prompt)
                .mapNotNull(ChatResponse::getResult)
                .mapNotNull(Generation::getOutput)
                .flatMap(message -> {
                    List<ChatToken> tokens = new ArrayList<>();
                    Object reasoning = message.getMetadata().get("reasoningContent");
                    if (reasoning instanceof String r && !r.isEmpty()) {
                        tokens.add(ChatToken.thinking(r));
                    }
                    String raw = message.getText();
                    if (raw == null || raw.isEmpty()) {
                        return Flux.fromIterable(tokens);
                    }
                    String content = sanitizeThinkAcrossChunks(raw, thinkBuf, inThinkBlock, thinkOpenRef[0], thinkCloseRef[0]);
                    if (content != null && !content.isEmpty()) {
                        tokens.add(ChatToken.content(content));
                    }
                    return Flux.fromIterable(tokens);
                })
                .doOnNext(token -> {
                    // 只累积正式回复内容，思考过程不持久化
                    if (token.isContent()) {
                        fullResponse.append(token.text());
                    }
                })
                .doOnComplete(() -> {
                    // 流结束：万一没收到结束标签，强制把缓冲里残余内容标记为非思考后清洗 fallback。
                    String tail = "";
                    if (inThinkBlock[0]) {
                        // 把这个未完成的缓冲作为普通 content 追加（不要让客户看不见任何东西）
                        tail = stripThinkTags(thinkBuf.toString());
                        thinkBuf.setLength(0);
                        inThinkBlock[0] = false;
                    }
                    if (fullResponse.isEmpty() && tail.isEmpty()) {
                        return;
                    }
                    if (!tail.isEmpty()) {
                        fullResponse.append(tail);
                    }
                    String reply = stripThinkTags(fullResponse.toString());
                    if (reply.isEmpty()) {
                        return;
                    }
                    conversation.add(new AssistantMessage(reply));
                    // 持久化裸文本（元数据由 Conversation 投影层按需拼前缀，DB 保持干净）
                    persistTurn(conversation, text, userCreatedAt, reply, LocalDateTime.now());
                })
                .doOnError(e -> log.error("Web 聊天流式响应失败: sessionId={}", sessionId, e));
    }

    /**
     * 将一轮 Web 对话的 user + assistant 两条消息写入数据库（source='web'）。
     * 单独提出方便出错时不影响流式完成。
     */
    private void persistTurn(Conversation conversation, String userText, LocalDateTime userCreatedAt,
                             String assistantText, LocalDateTime assistantCreatedAt) {
        try {
            MessageBO userBO = buildMessageBO(conversation, MessageBO.SENDER_USER, userText, userCreatedAt);
            MessageBO assistantBO = buildMessageBO(conversation, MessageBO.SENDER_ASSISTANT, assistantText, assistantCreatedAt);
            messageService.saveAll(List.of(userBO, assistantBO));
        } catch (Exception e) {
            log.error("Web 聊天消息持久化失败: sessionId={}", conversation.sessionId(), e);
        }
    }

    private MessageBO buildMessageBO(Conversation conversation, String sender, String content, LocalDateTime createTime) {
        MessageBO bo = new MessageBO();
        bo.setUserId(conversation.getUserId());
        bo.setDeviceId(conversation.getOwnerId());
        bo.setSessionId(conversation.sessionId());
        bo.setSource(MessageBO.SOURCE_WEB);
        bo.setSender(sender);
        bo.setMessage(content);
        bo.setRoleId(conversation.getRoleId());
        bo.setMessageType(MessageBO.MESSAGE_TYPE_NORMAL);
        bo.setCreateTime(createTime);
        return bo;
    }

    /**
     * 关闭 Web 聊天会话，释放资源
     */
    public void closeSession(String sessionId) {
        conversations.remove(sessionId);
        chatModels.remove(sessionId);
        log.info("Web 聊天会话已关闭: sessionId={}", sessionId);
    }

    /**
     * 检查会话是否存在
     */
    public boolean hasSession(String sessionId) {
        return conversations.containsKey(sessionId);
    }
}
