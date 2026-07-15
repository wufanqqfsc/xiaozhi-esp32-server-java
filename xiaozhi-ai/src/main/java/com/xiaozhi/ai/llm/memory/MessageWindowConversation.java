package com.xiaozhi.ai.llm.memory;

import lombok.Builder;
import org.springframework.ai.chat.messages.*;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
/**
 * 限定消息条数（消息窗口）的Conversation实现。根据不同的策略，可实现聊天会话的持久化、加载、清除等功能。
 * 短期记忆，只能记住当前对话有限的消息条数（多轮）。
 */
@Slf4j
public class MessageWindowConversation extends Conversation {
    private final int maxMessages;
    /**
     * 可切换加载维度的构造器。由 Lombok {@link Builder} 生成静态工厂 {@code builder()} 与链式 setter。
     * <ul>
     *   <li>{@code sessionScoped=false}（默认）：按 ownerId + roleId 查 {@link ChatMemory#find(String, int, int)}，设备场景跨 session 聚合</li>
     *   <li>{@code sessionScoped=true}：按 sessionId 查 {@link ChatMemory#find(String, int)}，Web 场景按会话隔离</li>
     * </ul>
     */
    @Builder
    public MessageWindowConversation(String ownerId, Integer roleId, String sessionId, String roleDesc, Integer userId,
                                      int maxMessages, ChatMemory chatMemory, boolean sessionScoped){
        super(ownerId, roleId, sessionId, roleDesc, userId);
        this.maxMessages = maxMessages;

        List<Message> history = sessionScoped
                ? chatMemory.find(sessionId, maxMessages)
                : chatMemory.find(ownerId, roleId, maxMessages);
        log.info("加载对话历史: sessionScoped={}, ownerId={}, sessionId={}, size={}",
                sessionScoped, ownerId, sessionId, history.size());
        super.messages.addAll(history);
        // 加载后立即清理历史中孤立的 tool result（无匹配 assistant.toolCalls）
        int removed = purgeOrphanToolResults(messages);
        if (removed > 0) {
            log.warn("加载历史时清理 {} 条孤立 tool result（ownerId={} roleId={} sessionId={}）",
                    removed, ownerId, roleId, sessionId);
        }
    }

    @Override
    public synchronized void add(Message message) {
        if (message instanceof UserMessage || message instanceof AssistantMessage || message instanceof ToolResponseMessage) {
            messages.add(message);
        } else {
            log.warn("不支持的消息类型：{}",message.getClass().getName());
        }
    }

    /**
     * 返回带系统提示词的消息列表，接受运行时上下文（位置、声纹等）
     */
    public synchronized List<Message> messages(ConversationContext context) {
        // 按对话组裁剪：简单组=[User,Assistant](2条)，工具组=[User,Assistant(toolCall),Tool,Assistant(final)](4条)
        while (messages.size() > maxMessages + 1) {
            if (messages.size() >= 2 && messages.get(1) instanceof AssistantMessage am
                    && am.getToolCalls() != null && !am.getToolCalls().isEmpty()
                    && messages.size() >= 4) {
                // 工具对话组：移除 4 条 [User, Assistant(toolCall), Tool, Assistant(final)]
                for (int i = 0; i < 4 && !messages.isEmpty(); i++) {
                    messages.remove(0);
                }
            } else {
                // 简单对话组：移除 2 条 [User, Assistant]
                messages.remove(0);
                if (!messages.isEmpty()) {
                    messages.remove(0);
                }
            }
        }
        // 兜底：返回前再过滤一次孤立 tool result（防止 messages 已被外部修改）
        List<Message> safeMessages = new ArrayList<>();
        Set<String> validToolIds = collectValidToolCallIds(messages);
        for (Message m : messages) {
            if (m instanceof ToolResponseMessage trm) {
                // 仅保留所有 toolResponse.id 都在 validToolIds 中的（保守：任一不在即整条丢弃）
                boolean allMatch = trm.getResponses().stream()
                        .allMatch(r -> r.id() != null && validToolIds.contains(r.id()));
                if (!allMatch) {
                    log.debug("过滤孤立 ToolResponseMessage（id={}）", trm.getResponses().stream()
                            .map(org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse::id)
                            .toList());
                    continue;
                }
            }
            safeMessages.add(m);
        }
        // 新消息列表对象，避免使用过程中污染原始列表对象
        List<Message> historyMessages = new ArrayList<>();
        var roleSystemMessage = roleSystemMessage(context);
        if(roleSystemMessage.isPresent()){
            historyMessages.add(roleSystemMessage.get());
        }
        historyMessages.addAll(safeMessages);
        // UserMessage 按 metadata 装配带前缀的副本供 LLM 使用
        return historyMessages.stream().map(UserMessageAssembler::assemble).toList();
    }

    /**
     * 收集 messages 中所有 assistant 消息里 tool_call 的 id 集合。
     */
    private static Set<String> collectValidToolCallIds(List<Message> msgs) {
        Set<String> ids = new HashSet<>();
        for (Message m : msgs) {
            if (m instanceof AssistantMessage am && am.getToolCalls() != null) {
                for (AssistantMessage.ToolCall tc : am.getToolCalls()) {
                    if (tc.id() != null) ids.add(tc.id());
                }
            }
        }
        return ids;
    }

    /**
     * 从 messages 中移除"孤立"的 ToolResponseMessage：即该 tool 消息的 id 不在
     * 任何 assistant 消息的 tool_calls 中。
     * 解决 MiniMax API 400 (2013) "tool result's tool id not found" 的问题。
     *
     * @return 实际移除的消息条数
     */
    private static int purgeOrphanToolResults(List<Message> msgs) {
        Set<String> validIds = collectValidToolCallIds(msgs);
        int removed = 0;
        Iterator<Message> it = msgs.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m instanceof ToolResponseMessage trm) {
                boolean hasOrphan = trm.getResponses().stream()
                        .anyMatch(r -> r.id() == null || !validIds.contains(r.id()));
                if (hasOrphan) {
                    it.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    @Override
    public synchronized List<Message> messages() {
        return messages(ConversationContext.EMPTY);
    }

}
