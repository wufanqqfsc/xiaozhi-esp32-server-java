package com.xiaozhi.communication.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xiaozhi.common.model.bo.DeviceBO;
import com.xiaozhi.dialogue.runtime.Persona;
import com.xiaozhi.ai.stt.SttServiceFactory;
import com.xiaozhi.token.TokenService;
import com.xiaozhi.ai.tts.TtsServiceFactory;
import com.xiaozhi.common.model.bo.ConfigBO;
import com.xiaozhi.config.service.ConfigService;
import com.xiaozhi.device.service.DeviceService;
import com.xiaozhi.role.service.RoleService;
import com.xiaozhi.utils.JsonUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
/**
 * Redis 消息订阅配置。
 * 监听跨实例广播，在本实例执行对应操作。
 * <p>
 * 使用 Redisson RTopic 实现可靠的 Pub/Sub 订阅。
 * 之前使用的 Spring Data Redis RedisMessageListenerContainer 在 Redisson 连接工厂下存在
 * 订阅超时问题（Subscription registration timeout exceeded），因此改用 Redisson 原生 RTopic。
 */
@Slf4j
@Configuration
public class RedisSubscriber {

    @Resource
    private SessionManager sessionManager;

    @Resource
    private DeviceRegistry deviceRegistry;

    @Resource
    private SttServiceFactory sttServiceFactory;

    @Resource
    private TtsServiceFactory ttsServiceFactory;

    @Resource
    private TokenService tokenService;

    @Resource
    private ConfigService configService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private CacheManager cacheManager;

    @PostConstruct
    public void initRedissonTopics() {
        try {
            // 使用 StringCodec 避免 JsonJacksonCodec 解码原始字符串失败的问题
            // （发布侧使用 StringRedisTemplate.convertAndSend 发送原始字符串）
            RTopic clearConversationTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_CLEAR_CONVERSATION, StringCodec.INSTANCE);
            clearConversationTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onClearConversation(msg);
            });

            RTopic roleChangedTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_ROLE_CHANGED, StringCodec.INSTANCE);
            roleChangedTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onRoleChanged(msg);
            });

            RTopic configChangedTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_CONFIG_CHANGED, StringCodec.INSTANCE);
            configChangedTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onConfigChanged(msg);
            });

            RTopic closeSessionTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_CLOSE_SESSION, StringCodec.INSTANCE);
            closeSessionTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onCloseSession(msg);
            });

            RTopic roleUpdatedTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_ROLE_UPDATED, StringCodec.INSTANCE);
            roleUpdatedTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onRoleUpdated(msg);
            });

            RTopic deviceUpdatedTopic = redissonClient.getTopic(RedisBroadcast.CHANNEL_DEVICE_UPDATED, StringCodec.INSTANCE);
            deviceUpdatedTopic.addListener(String.class, (channel, msg) -> {
                log.debug("Redisson 收到消息 - channel: {}, message: {}", channel, msg);
                onDeviceUpdated(msg);
            });

            log.info("Redisson RTopic 订阅初始化完成 - 6 个频道已注册 (StringCodec)");
        } catch (Exception e) {
            log.error("Redisson RTopic 订阅初始化失败", e);
        }
    }

    /**
     * 清除对话历史
     */
    public void onClearConversation(String deviceId) {
        sessionManager.findConversation(deviceId).ifPresent(conversation -> {
            conversation.clear();
            log.info("已清除设备对话历史（来自跨实例广播） - deviceId: {}", deviceId);
        });
    }

    /**
     * 设备角色变更：清理 Persona，下次唤醒时重新构建
     */
    public void onRoleChanged(String deviceId) {
        ChatSession session = sessionManager.getSessionByDeviceId(deviceId);
        if (session != null) {
            // 先从 DB 刷新 device（含新 roleId），否则重建 Persona 时仍用旧角色
            DeviceBO freshDevice = deviceService.getBO(deviceId);
            if (freshDevice != null) {
                freshDevice.setSessionId(session.getSessionId());
                session.setDevice(freshDevice);
            }
            Persona persona = session.getPersona();
            if (persona != null) {
                persona.getConversation().clear();
                session.setPersona(null);
            }
            log.info("已清理设备 Persona（来自跨实例广播） - deviceId: {}", deviceId);
        }
    }

    /**
     * 角色属性变更（如角色描述、音色等）：遍历本实例 session，清理使用该角色的 Persona，
     * 同时清除角色缓存，确保下次构建 Persona 时从数据库加载最新配置。
     */
    public void onRoleUpdated(String message) {
        try {
            Integer roleId = Integer.parseInt(message.trim());
            int count = 0;
            for (ChatSession session : sessionManager.getAllSessions()) {
                DeviceBO device = session.getDevice();
                if (device != null && roleId.equals(device.getRoleId())) {
                    Persona persona = session.getPersona();
                    if (persona != null) {
                        persona.getConversation().clear();
                        session.setPersona(null);
                        count++;
                    }
                }
            }
            Cache roleCache = cacheManager.getCache(RoleService.CACHE_NAME);
            if (roleCache != null) {
                roleCache.evict(String.valueOf(roleId));
                log.debug("已清除角色缓存 - roleId: {}", roleId);
            }
            if (count > 0) {
                log.info("角色属性变更，已清理 {} 个 Persona（roleId: {}）", count, roleId);
            } else {
                log.debug("角色属性变更，无活跃 Persona 需要清理（roleId: {}）", roleId);
            }
        } catch (Exception e) {
            log.error("处理 roleUpdated 广播失败", e);
        }
    }

    /**
     * 关闭设备会话：只有设备在本实例时才处理
     */
    public void onCloseSession(String deviceId) {
        ChatSession session = sessionManager.getSessionByDeviceId(deviceId);
        if (session != null) {
            sessionManager.closeSession(session);
            log.info("已关闭设备会话（来自跨实例广播） - deviceId: {}", deviceId);
        }
    }

    /**
     * 设备信息变更：刷新本实例中该设备的 session 数据
     */
    public void onDeviceUpdated(String deviceId) {
        ChatSession session = sessionManager.getSessionByDeviceId(deviceId);
        if (session != null) {
            DeviceBO freshDevice = deviceService.getBO(deviceId);
            if (freshDevice != null) {
                freshDevice.setSessionId(session.getSessionId());
                session.setDevice(freshDevice);
                log.info("已刷新设备信息（来自跨实例广播） - deviceId: {}", deviceId);
            }
        }
    }

    /**
     * 配置变更：清除对应工厂缓存（STT/TTS/Token）
     */
    public void onConfigChanged(String message) {
        try {
            Map<String, Object> payload = JsonUtil.fromJson(message, new TypeReference<>() {});
            String configType = (String) payload.get("configType");
            Integer configId = (Integer) payload.get("configId");

            ConfigBO config = configService.getBO(configId);
            if (config != null) {
                if ("stt".equals(configType)) {
                    sttServiceFactory.removeCache(config);
                } else if ("tts".equals(configType)) {
                    ttsServiceFactory.removeCache(config);
                }
                // Token 缓存（Coze OAuth、阿里云 Token 等）与 configType 无关，统一清除
                tokenService.removeCache(config);
                log.info("已清除工厂缓存 - configType: {}, configId: {}", configType, configId);
            }
        } catch (Exception e) {
            log.error("处理 configChanged 广播失败", e);
        }
    }
}
