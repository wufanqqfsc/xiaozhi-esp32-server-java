package com.xiaozhi.ai.stt.providers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaozhi.ai.stt.SttResult;
import com.xiaozhi.ai.stt.SttService;
import com.xiaozhi.common.model.bo.ConfigBO;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.xiaozhi.common.utils.LatencyTracer;

import lombok.extern.slf4j.Slf4j;
/**
 * FunASR STT服务实现 (支持 Paraformer / SenseVoice)
 */
@Slf4j
public class FunASRSttService implements SttService {

    private static final String PROVIDER_NAME = "funasr";

    private static final String SPEAKING_START = "{\"mode\":\"2pass\",\"wav_name\":\"voice.wav\",\"is_speaking\":true,\"wav_format\":\"pcm\",\"itn\":true,\"chunk_size\":[5,10,5]}";
    private static final String SPEAKING_END = "{\"is_speaking\": false}";

    private static final Pattern SENSEVOICE_TAG_PATTERN =
            Pattern.compile("<\\|[^|]*\\|>");

    private static final int QUEUE_TIMEOUT_MS = 100;
    private static final long RECOGNITION_TIMEOUT_MS = 90000;
    private static final long GRACEFUL_CLOSE_WAIT_MS = 2000;

    private final String apiUrl;

    public FunASRSttService(ConfigBO config) {
        this.apiUrl = config.getApiUrl();
    }

    private String cleanSenseVoiceText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return SENSEVOICE_TAG_PATTERN.matcher(text).replaceAll("").trim();
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public SttResult stream(Flux<byte[]> audioSink) {
        LatencyTracer.start(LatencyTracer.currentSession(), "STT_RECV");
        BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();
        AtomicBoolean isCompleted = new AtomicBoolean(false);
        StringBuilder offlineResult = new StringBuilder();
        AtomicReference<String> finalResult = new AtomicReference<>("");
        CountDownLatch recognitionLatch = new CountDownLatch(1);

        audioSink.subscribe(
            data -> audioQueue.offer(data),
            error -> {
                log.error("音频流处理错误", error);
                isCompleted.set(true);
            },
            () -> isCompleted.set(true)
        );

        WebSocketClient webSocketClient = new WebSocketClient(URI.create(apiUrl)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                log.debug("FunASR WebSocket连接已打开");
                send(SPEAKING_START);

                Thread.startVirtualThread(() -> {
                    try {
                        while (!isCompleted.get() || !audioQueue.isEmpty()) {
                            byte[] audioChunk = null;
                            try {
                                audioChunk = audioQueue.poll(QUEUE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                log.warn("音频数据队列等待被中断", e);
                                Thread.currentThread().interrupt();
                                break;
                            }

                            if (audioChunk != null && isOpen()) {
                                send(audioChunk);
                            }
                        }

                        if (isOpen()) {
                            send(SPEAKING_END);
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (Exception e) {
                        log.error("发送音频数据时发生错误", e);
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                try {
                    JSONObject jsonObject = JSON.parseObject(message);
                    Boolean isFinal = jsonObject.getBoolean("is_final");
                    String mode = jsonObject.getString("mode");
                    String text = jsonObject.getString("text");
                    log.debug("FunASR 收到消息: is_final={}, mode={}, text={}", isFinal, mode, text);

                    synchronized (offlineResult) {
                        boolean isFinalFlag = Boolean.TRUE.equals(isFinal);
                        if (text != null && !text.isEmpty()) {
                            text = cleanSenseVoiceText(text);
                            if (text.isEmpty()) {
                                return;
                            }
                            if ("offline".equals(mode) || "2pass-offline".equals(mode)) {
                                if (offlineResult.length() > 0) {
                                    offlineResult.append(" ");
                                }
                                offlineResult.append(text);
                                log.info("FunASR 离线识别结果: {}", text);
                            } else if ("2pass-online".equals(mode) && offlineResult.length() == 0) {
                                offlineResult.append(text);
                                log.info("FunASR 在线结果（离线未返回，使用兜底）: {}", text);
                            } else if (isFinalFlag && offlineResult.length() == 0) {
                                offlineResult.append(text);
                                log.info("FunASR 最终结果: {}", text);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("解析FunASR响应失败", e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("FunASR WS关闭，原因：{}", reason);
                try {
                    Thread.sleep(GRACEFUL_CLOSE_WAIT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (offlineResult) {
                    finalResult.set(offlineResult.toString());
                }
                LatencyTracer.mark(LatencyTracer.currentSession(), "STT_DONE");
                recognitionLatch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                log.error("FunASR WS错误", ex);
                synchronized (offlineResult) {
                    finalResult.set(offlineResult.toString());
                }
                LatencyTracer.mark(LatencyTracer.currentSession(), "STT_DONE");
                recognitionLatch.countDown();
            }
        };

        try {
            webSocketClient.connect();
            boolean recognized = recognitionLatch.await(RECOGNITION_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (!recognized) {
                log.warn("FunASR识别超时，等待音频发送线程结束...");
                isCompleted.set(true);
                try {
                    Thread.sleep(GRACEFUL_CLOSE_WAIT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            log.error("FunASR识别过程中发生错误", e);
        } finally {
            if (webSocketClient.isOpen()) {
                webSocketClient.close();
            }
        }

        String result = finalResult.get();
        if (result.isEmpty()) {
            // 超时后 onClose 可能尚未触发，finalResult 为空
            // 回退检查 offlineResult 是否已有识别文本
            synchronized (offlineResult) {
                result = offlineResult.toString();
            }
            if (!result.isEmpty()) {
                log.info("FunASR超时但已有识别结果: {}", result);
            } else {
                log.warn("FunASR识别结果为空");
            }
        } else {
            log.info("FunASR识别成功: {}", result);
        }
        return SttResult.textOnly(result);
    }
}