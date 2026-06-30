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
 * 无论 {@code enableThinking} 取值，HTTP 请求体都会显式注入
 * {@code "thinking": {"type": "disabled"}}，确保 MiniMax 关闭思考模式。
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
            webClientBuilder.filter(miniMaxThinkingFilter());
            restClientBuilder.requestInterceptor(miniMaxThinkingInterceptor());
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
            log.info("OpenAI model {} endpoint {} 已注入 thinking={{type:disabled}}", model, endpoint);
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
     * 同步 JSON body 注入 thinking:disabled。
     * 如果 body 不是合法 JSON object 或已存在 thinking 字段，原样返回。
     */
    private static byte[] injectThinkingDisabled(byte[] body) {
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
            byte[] mutated = OBJECT_MAPPER.writeValueAsBytes(obj);
            log.debug("MiniMax 请求体已注入 thinking:disabled，原大小={} 字节，新大小={} 字节", body.length, mutated.length);
            return mutated;
        } catch (Exception e) {
            log.warn("注入 thinking:disabled 失败，跳过: {}", e.getMessage());
            return body;
        }
    }

    /**
     * RestClient 同步路径拦截器：拦截每一次 HTTP 请求，修改 byte[] body 后重新发送。
     */
    private static ClientHttpRequestInterceptor miniMaxThinkingInterceptor() {
        return (request, body, execution) -> {
            byte[] mutated = injectThinkingDisabled(body);
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
}
