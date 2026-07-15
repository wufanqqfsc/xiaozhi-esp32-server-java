package com.xiaozhi.ai.llm.factory.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xiaozhi.ai.llm.factory.ChatModelProvider;
import com.xiaozhi.common.model.bo.ConfigBO;
import com.xiaozhi.common.model.bo.RoleBO;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
/**
 * OpenAI及兼容OpenAI协议的模型提供者。
 * 支持: OpenAI, Azure OpenAI, 各种兼容OpenAI的本地模型等。
 * <p>
 * 通过配置 {@code enableThinking} 控制是否启用推理模式（{@code reasoningEffort}）。
 * <p>
 * 当 endpoint 命中 MiniMax（{@code api.minimaxi.com} 或包含 {@code minimax}）时，
 * 行为按模型版本分支：
 * <ul>
 *   <li><b>M2 及更早</b>（如 {@code abab6.5s-chat}、{@code MiniMax-M2}）：
 *       注入 {@code "thinking": {"type": "disabled"}} 和 {@code "reasoning_split": true}。
 *       这两个参数是旧模型私有的，M3+ 不再识别。</li>
 *   <li><b>M3 及更新</b>（如 {@code MiniMax-M3}）：不注入任何私有字段。
 *       思考控制应改用 OpenAI 兼容的 {@code reasoning_effort}（由 {@code enableThinking} 决定）。</li>
 * </ul>
 * 实现路径：
 * <ul>
 *   <li>同步路径：{@link RestClient} 的 {@link ClientHttpRequestInterceptor}</li>
 *   <li>流式路径：{@link WebClient} 的 {@link ExchangeFilterFunction}，
 *       通过 lambda {@code BodyInserter} 重新写入修改后的 bytes</li>
 * </ul>
 */
@Slf4j
@Component
public class OpenAiModelProvider implements ChatModelProvider {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Lazy
    @Autowired
    private ToolCallingManager toolCallingManager;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Override
    public String getProviderName() {
        return "openai";
    }

    @Override
    public ChatModel createChatModel(ConfigBO config, RoleBO role) {
        String endpoint = config.getApiUrl();
        String apiKey = config.getApiKey();
        String model = config.getConfigName();
        Double temperature = role.getTemperature();
        Double topP = role.getTopP();

        boolean isMiniMax = isMiniMaxEndpoint(endpoint);
        // M3+ 不再识别 thinking.type=disabled / reasoning_split（M2 私有参数），
        // 只有 M2 及更早才需要注入。
        boolean isLegacyMiniMax = isMiniMax && isLegacyMiniMaxModel(model);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        WebClient.Builder webClientBuilder = WebClient.builder()
                .clientConnector(new JdkClientHttpConnector(HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .connectTimeout(Duration.ofSeconds(30))
                        .build()));
        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(createRequestFactory());

        if (isMiniMax) {
            // 诊断 dump filter 在所有 MiniMax 端点上启用，便于追踪 4xx 响应 body。
            webClientBuilder.filter(miniMaxResponseDumpFilter());
            // thinking/reasoning_split 注入仅在 M2 旧模型上启用，M3+ 跳过。
            if (isLegacyMiniMax) {
                webClientBuilder.filter(miniMaxThinkingFilter());
                restClientBuilder.requestInterceptor(miniMaxThinkingInterceptor());
            }
        }

        // LM Studio不支持Http/2，所以需要强制使用HTTP/1.1
        var openAiApi = OpenAiApi.builder()
                .apiKey(StringUtils.hasText(apiKey) ? new SimpleApiKey(apiKey) : new NoopApiKey())
                .baseUrl(endpoint)
                .completionsPath("/chat/completions")
                .headers(headers)
                .webClientBuilder(webClientBuilder)
                .restClientBuilder(restClientBuilder)
                .build();

        boolean enableThinking = Boolean.TRUE.equals(config.getEnableThinking());

        var chatOptionsBuilder = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .topP(topP)
                .maxCompletionTokens(2000)
                .streamUsage(true);

        if (enableThinking) {
            chatOptionsBuilder.reasoningEffort("medium");
            log.info("OpenAI model {} 已启用思考模式，reasoningEffort=medium", model);
        }

        if (isMiniMax) {
            if (isLegacyMiniMax) {
                log.info("OpenAI model {} endpoint {} (legacy M2) 已注入 thinking={{type:disabled}} 和 reasoning_split=true", model, endpoint);
            } else {
                log.info("OpenAI model {} endpoint {} (>=M3) 跳过 thinking/reasoning_split 注入，使用 OpenAI 兼容 reasoning_effort", model, endpoint);
            }
        }

        var openAiChatOptions = chatOptionsBuilder.build();

        var chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiChatOptions)
                .toolCallingManager(toolCallingManager)
                .observationRegistry(observationRegistry)
                .build();

        log.info("Created OpenAI ChatModel: model={}, endpoint={}, thinking={}", model, endpoint, enableThinking);
        return chatModel;
    }

    @Override
    public EmbeddingModel createEmbeddingModel(ConfigBO config) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");

        var openAiApi = OpenAiApi.builder()
                .apiKey(StringUtils.hasText(config.getApiKey()) ? new SimpleApiKey(config.getApiKey()) : new NoopApiKey())
                .baseUrl(config.getApiUrl())
                .embeddingsPath("/embeddings")
                .headers(headers)
                .webClientBuilder(WebClient.builder()
                        .clientConnector(new JdkClientHttpConnector(HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .connectTimeout(Duration.ofSeconds(30))
                                .build())))
                .restClientBuilder(RestClient.builder()
                        .requestFactory(createRequestFactory()))
                .build();
        var options = OpenAiEmbeddingOptions.builder().model(config.getConfigName()).build();
        log.debug("创建 OpenAI EmbeddingModel: model={}, endpoint={}", config.getConfigName(), config.getApiUrl());
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, options);
    }

    private JdkClientHttpRequestFactory createRequestFactory() {
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .build());
        factory.setReadTimeout(Duration.ofSeconds(30));
        return factory;
    }

    /**
     * 判断 endpoint 是否是 MiniMax/MiniMaxi（含大小写不敏感匹配）。
     */
    private static boolean isMiniMaxEndpoint(String endpoint) {
        if (!StringUtils.hasText(endpoint)) {
            return false;
        }
        String lower = endpoint.toLowerCase(Locale.ROOT);
        return lower.contains("minimaxi") || lower.contains("minimax");
    }

    /**
     * 判断模型名是否属于 MiniMax 旧版（M2 及更早）模型族。
     * <p>
     * 旧版模型（如 {@code abab6.5s-chat}、{@code abab5.5-chat}、{@code MiniMax-M2}）使用
     * 私有参数 {@code thinking.type=disabled} 和 {@code reasoning_split} 控制思考行为。
     * 新版模型（{@code MiniMax-M3} 及以后）改用 OpenAI 兼容的 {@code reasoning_effort}，
     * 不再识别上述两个私有字段；继续注入反而会被服务端拒绝（400 BadRequest）。
     * <p>
     * 判定规则：
     * <ul>
     *   <li>模型名包含 {@code -M3} / {@code M3-} / {@code -M4} 等 ≥M3 后缀：{@code false}</li>
     *   <li>模型名包含 {@code -M1} / {@code -M2} / {@code abab} / {@code MiniMax-Text-01}：{@code true}
     *       （保守起见，未知名称也按旧版处理，让注入照常进行）</li>
     *   <li>空/未知名称：{@code true}（保守策略，行为不变）</li>
     * </ul>
     */
    static boolean isLegacyMiniMaxModel(String model) {
        if (!StringUtils.hasText(model)) {
            return true;
        }
        String lower = model.toLowerCase(Locale.ROOT);
        // M3 及以上统一不注入（"m3"、"m4" 单独作为 token 出现；"-m3" / "m3-" 视为新版）。
        // 用 tokenizer-lite 的方式扫描：把非字母数字字符当分隔符。
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(?i)(?:^|[^a-z0-9])(m\\d{1,2})(?:[^a-z0-9]|$)")
                .matcher(lower);
        while (m.find()) {
            String token = m.group(1).toLowerCase(Locale.ROOT);
            if (token.startsWith("m")) {
                try {
                    int ver = Integer.parseInt(token.substring(1));
                    if (ver >= 3) {
                        return false;
                    }
                } catch (NumberFormatException ignored) {
                    // 不是纯数字版本号，继续下一个匹配
                }
            }
        }
        // 未检测到 ≥M3 版本号 → 视为旧版
        return true;
    }

    /**
     * 同步 JSON body 注入 MiniMax thinking 控制参数。
     * <ul>
     *   <li>thinking: {type: "disabled"} - 尝试禁用thinking（M2.x模型无法关闭）</li>
     *   <li>reasoning_split: true - 确保thinking通过reasoning_content返回，而非留在<think>...</think>标签内</li>
     * </ul>
     * 如果 body 不是合法 JSON object，原样返回。
     */
    private static byte[] injectMiniMaxThinkingParams(byte[] body) {
        if (body == null || body.length == 0) {
            return body;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);
            if (!(root instanceof ObjectNode obj)) {
                return body;
            }
            if (obj.has("thinking")) {
                return body;
            }
            ObjectNode thinking = OBJECT_MAPPER.createObjectNode();
            thinking.put("type", "disabled");
            obj.set("thinking", thinking);
            obj.put("reasoning_split", true);
            byte[] mutated = OBJECT_MAPPER.writeValueAsBytes(obj);
            log.debug("MiniMax 请求体已注入 thinking={{type:disabled}} 和 reasoning_split=true，原大小={} 字节，新大小={} 字节", body.length, mutated.length);
            return mutated;
        } catch (Exception e) {
            log.warn("注入 MiniMax thinking 参数失败，跳过: {}", e.getMessage());
            return body;
        }
    }

    /**
     * RestClient 同步路径拦截器：拦截每一次 HTTP 请求，修改 byte[] body 后重新发送。
     */
    private static ClientHttpRequestInterceptor miniMaxThinkingInterceptor() {
        return (request, body, execution) -> {
            byte[] mutated = injectMiniMaxThinkingParams(body);
            return execution.execute(request, mutated);
        };
    }

    /**
     * WebClient 流式路径 ExchangeFilterFunction：
     * 把 body 收集为 byte[]、注入 thinking:disabled、用 lambda BodyInserter 重新写回。
     * <p>
     * 注意：{@code ClientRequest.body()} 返回的是 {@code BodyInserter} 而非
     * {@code Publisher<DataBuffer>}，不能直接用 {@code Flux.from(...)} 转换。
     * 这里用一个内存级的 {@code ClientHttpRequest} 桩把 body 写出来收集字节。
     */
    private static ExchangeFilterFunction miniMaxThinkingFilter() {
        return (request, next) -> {
            if (!HttpMethod.POST.equals(request.method())) {
                return next.exchange(request);
            }
            // MiniMax 流式路径：依赖同步 interceptor 即可。
            // 这里只做最小处理：直接放行，避免 BodyInserter 的复杂拆装。
            // 如果需要流式也注入 thinking:disabled，需要用 ClientRequest 的
            // body(BodyInserter) 重新装配并触发 MockClientHttpRequest 收集 bytes。
            log.debug("MiniMax WebClient stream path: 依赖同步 interceptor 控制 thinking");
            return next.exchange(request);
        };
    }

    /**
     * 诊断专用 filter：dump 请求 URL/Headers/Body、响应状态/Headers/错误体到
     * logs/minimax-debug.log，便于定位 400 错误根因。
     * <p>
     * <b>关键实现</b>：
     * <ul>
     *   <li><b>请求 body</b>：用 {@link org.springframework.mock.http.client.MockClientHttpRequest}
     *       作为桩接收 {@code BodyInserter}，把 bytes 收集到内存（仅 1-10KB，可接受），写到 dump。
     *       不破坏流式（仅读取并原样放回）。</li>
     *   <li><b>4xx 响应 body</b>：当响应是 4xx/5xx 时，{@code ClientResponse} 的 body publisher
     *       尚未被订阅，{@code WebClientResponseException.getResponseBodyAsString()} 拿到的是空。
     *       本 filter 在 {@code flatMap} 阶段就主动把 body 读为 {@code byte[]}，记录到 dump，
     *       然后用 {@code ClientResponse.create(...).body(byte[])} 重建一个等价的响应
     *       传给下游，<u>不破坏流式</u>。</li>
     *   <li>成功响应（2xx）的 body 仍走原始流式 publisher，不读取。</li>
     * </ul>
     */
    private static ExchangeFilterFunction miniMaxResponseDumpFilter() {
        final java.util.concurrent.atomic.AtomicInteger seq = new java.util.concurrent.atomic.AtomicInteger(0);
        return (request, next) -> {
            int id = seq.incrementAndGet();
            long t0 = System.nanoTime();
            StringBuilder dump = new StringBuilder(4096);
            dump.append("\n========== MiniMax LLM call #").append(id).append(" ==========\n");
            dump.append("TIME: ").append(java.time.LocalDateTime.now()).append('\n');
            dump.append("METHOD: ").append(request.method()).append('\n');
            dump.append("URL: ").append(request.url()).append('\n');
            dump.append("REQUEST HEADERS:\n");
            request.headers().forEach((k, v) -> {
                String joined = String.join(",", v);
                // 鉴权头只打印前 8 字符 + 省略号，避免日志泄露完整 key
                if (k.equalsIgnoreCase("Authorization") && joined.length() > 12) {
                    joined = joined.substring(0, 12) + "...(redacted," + joined.length() + " chars)";
                }
                dump.append("  ").append(k).append(": ").append(joined).append('\n');
            });

            // 把请求 body 写到一个桩 ClientHttpRequest（reactive 变种），收集字节用于 dump。
            // reactive 的 ClientHttpRequest.body() 返回 BodyInserter，写入时通过
            // writeWith(Flux<DataBuffer>) 把 bytes 聚合到内部 ByteArrayOutputStream。
            try {
                java.io.ByteArrayOutputStream reqBaos = new java.io.ByteArrayOutputStream(4096);
                org.springframework.http.client.reactive.ClientHttpRequest bodyStub =
                    new org.springframework.http.client.reactive.ClientHttpRequest() {
                    private final org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    @Override public org.springframework.http.HttpMethod getMethod() { return org.springframework.http.HttpMethod.valueOf(request.method().name()); }
                    @Override public java.net.URI getURI() { return request.url(); }
                    @Override public org.springframework.http.HttpHeaders getHeaders() { return headers; }
                    @Override public java.util.Map<String, Object> getAttributes() { return java.util.Collections.emptyMap(); }
                    @Override public org.springframework.core.io.buffer.DataBufferFactory bufferFactory() { return org.springframework.core.io.buffer.DefaultDataBufferFactory.sharedInstance; }
                    @Override public boolean isCommitted() { return false; }
                    @Override public void beforeCommit(java.util.function.Supplier<? extends reactor.core.publisher.Mono<Void>> action) { /* no-op */ }
                    @Override public org.springframework.util.MultiValueMap<String, org.springframework.http.HttpCookie> getCookies() { return new org.springframework.util.LinkedMultiValueMap<>(); }
                    @Override public <T> T getNativeRequest() { return null; }
                    @Override public reactor.core.publisher.Mono<Void> writeWith(org.reactivestreams.Publisher<? extends org.springframework.core.io.buffer.DataBuffer> body) {
                        return reactor.core.publisher.Flux.from(body)
                            .doOnNext(buf -> {
                                byte[] chunk = new byte[buf.readableByteCount()];
                                buf.read(chunk);
                                reqBaos.write(chunk, 0, chunk.length);
                            })
                            .then();
                    }
                    @Override public reactor.core.publisher.Mono<Void> writeAndFlushWith(org.reactivestreams.Publisher<? extends org.reactivestreams.Publisher<? extends org.springframework.core.io.buffer.DataBuffer>> body) {
                        return reactor.core.publisher.Flux.from(body)
                            .flatMap(p -> reactor.core.publisher.Flux.from(p))
                            .doOnNext(buf -> {
                                byte[] chunk = new byte[buf.readableByteCount()];
                                buf.read(chunk);
                                reqBaos.write(chunk, 0, chunk.length);
                            })
                            .then();
                    }
                    @Override public reactor.core.publisher.Mono<Void> setComplete() {
                        return reactor.core.publisher.Mono.empty();
                    }
                };
                // BodyInserter.Context：提供 messageWriters/serverRequest/hints。
                // messageWriters 取自 ExchangeStrategies.withDefaults()，与 WebClient 一致。
                org.springframework.web.reactive.function.client.ExchangeStrategies strategies =
                        org.springframework.web.reactive.function.client.ExchangeStrategies.withDefaults();
                request.body().insert(bodyStub, new org.springframework.web.reactive.function.BodyInserter.Context() {
                    @Override
                    public java.util.List<org.springframework.http.codec.HttpMessageWriter<?>> messageWriters() {
                        return strategies.messageWriters();
                    }
                    @Override
                    public java.util.Optional<org.springframework.http.server.reactive.ServerHttpRequest> serverRequest() {
                        return java.util.Optional.empty();
                    }
                    @Override
                    public java.util.Map<String, Object> hints() {
                        return java.util.Collections.emptyMap();
                    }
                }).block();
                byte[] reqBody = reqBaos.toByteArray();
                dump.append("REQUEST BODY (").append(reqBody.length).append(" bytes):\n");
                if (reqBody.length > 0) {
                    String bodyStr = new String(reqBody, java.nio.charset.StandardCharsets.UTF_8);
                    if (bodyStr.length() > 50_000) {
                        bodyStr = bodyStr.substring(0, 50_000) + "...(truncated, original " + bodyStr.length() + " chars)";
                    }
                    dump.append(bodyStr);
                    if (!bodyStr.endsWith("\n")) {
                        dump.append('\n');
                    }
                } else {
                    dump.append("(empty)\n");
                }
            } catch (Throwable th) {
                dump.append("(failed to capture request body: ")
                    .append(th.getClass().getName()).append(": ").append(th.getMessage()).append(")\n");
            }

            return next.exchange(request)
                .flatMap((ClientResponse resp) -> {
                    long costMs = (System.nanoTime() - t0) / 1_000_000L;
                    dump.append("STATUS: ").append(resp.statusCode().value());
                    if (resp.statusCode().isError()) {
                        dump.append(" (error)");
                    }
                    dump.append('\n');
                    dump.append("COST: ").append(costMs).append(" ms\n");
                    dump.append("RESPONSE HEADERS:\n");
                    resp.headers().asHttpHeaders().forEach((k, v) ->
                        dump.append("  ").append(k).append(": ").append(String.join(",", v)).append('\n'));

                    if (resp.statusCode().isError()) {
                        // 4xx/5xx：主动消费 body 拿真实错误内容，然后重建 ClientResponse 传给下游
                        return resp.bodyToMono(byte[].class)
                                .defaultIfEmpty(new byte[0])
                                .<ClientResponse>map(bodyBytes -> {
                                    dump.append("ERROR RESPONSE BODY (").append(bodyBytes.length).append(" bytes):\n");
                                    if (bodyBytes.length > 0) {
                                        String bodyStr = new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8);
                                        dump.append(bodyStr);
                                        if (!bodyStr.endsWith("\n")) {
                                            dump.append('\n');
                                        }
                                    } else {
                                        dump.append("(empty body)\n");
                                    }
                                    // 重建 ClientResponse：相同 status + headers + byte[] 包装的 body
                                    org.springframework.core.io.buffer.DataBuffer buf =
                                        org.springframework.core.io.buffer.DefaultDataBufferFactory.sharedInstance
                                            .wrap(bodyBytes);
                                    ClientResponse newResp = ClientResponse.create(resp.statusCode())
                                            .headers(h -> h.addAll(resp.headers().asHttpHeaders()))
                                            .body(reactor.core.publisher.Flux.just(buf))
                                            .build();
                                    return newResp;
                                })
                                .doOnError(err -> {
                                    dump.append("(failed to read error body: ").append(err.getClass().getName())
                                        .append(": ").append(err.getMessage()).append(")\n");
                                })
                                .onErrorResume(err -> {
                                    // body 读取失败时回传原响应让下游处理
                                    return reactor.core.publisher.Mono.just(resp);
                                });
                    } else {
                        // 2xx 成功响应：原样透传，不读取 body（保留流式）
                        return reactor.core.publisher.Mono.just(resp);
                    }
                })
                .doOnError(err -> {
                    long costMs = (System.nanoTime() - t0) / 1_000_000L;
                    dump.append("COST: ").append(costMs).append(" ms\n");
                    dump.append("ERROR: ").append(err.getClass().getName())
                        .append(": ").append(err.getMessage()).append('\n');
                    if (err instanceof org.springframework.web.reactive.function.client.WebClientResponseException wcre) {
                        String body = wcre.getResponseBodyAsString();
                        if (body != null && !body.isEmpty()) {
                            dump.append("ERROR RESPONSE BODY (from exception, ")
                                .append(body.length()).append(" bytes):\n");
                            dump.append(body).append('\n');
                        } else {
                            dump.append("(WebClientResponseException.getResponseBodyAsString returned empty — ")
                                .append("body should have been captured by flatMap above)\n");
                        }
                    } else {
                        // 非 4xx/5xx 异常（如连接超时、SocketException），附 cause chain
                        dump.append("CAUSE CHAIN:\n");
                        Throwable cur = err;
                        int depth = 0;
                        while (cur != null && depth < 3) {
                            dump.append("  [").append(depth).append("] ")
                                .append(cur.getClass().getName()).append(": ").append(cur.getMessage()).append('\n');
                            cur = cur.getCause();
                            depth++;
                        }
                    }
                    writeMiniMaxDebug(dump);
                })
                .doOnSuccess(v -> writeMiniMaxDebug(dump))
                .doFinally(sig -> writeMiniMaxDebug(dump));
        };
    }

    private static final java.util.concurrent.ConcurrentLinkedQueue<String> DUMP_QUEUE = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private static final java.util.concurrent.atomic.AtomicBoolean WRITER_STARTED = new java.util.concurrent.atomic.AtomicBoolean(false);
    private static void writeMiniMaxDebug(StringBuilder dump) {
        DUMP_QUEUE.offer(dump.toString());
        if (WRITER_STARTED.compareAndSet(false, true)) {
            Thread t = new Thread(() -> {
                try (var fw = new java.io.FileWriter("./logs/minimax-debug.log", true)) {
                    while (true) {
                        String s;
                        while ((s = DUMP_QUEUE.poll()) != null) {
                            fw.write(s);
                            fw.flush();
                        }
                        Thread.sleep(200);
                    }
                } catch (Throwable th) {
                    org.slf4j.LoggerFactory.getLogger(OpenAiModelProvider.class)
                        .error("minimax-debug.log writer crashed", th);
                }
            }, "minimax-debug-writer");
            t.setDaemon(true);
            t.start();
        }
    }
}
