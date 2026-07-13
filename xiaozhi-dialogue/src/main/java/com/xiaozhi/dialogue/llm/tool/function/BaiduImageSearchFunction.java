package com.xiaozhi.dialogue.llm.tool.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaozhi.ai.llm.tool.ToolCallStringResultConverter;
import com.xiaozhi.ai.tool.ToolsGlobalRegistry;
import com.xiaozhi.ai.tool.session.ToolSession;
import com.xiaozhi.communication.ServerAddressProvider;
import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.communication.common.SessionManager;
import com.xiaozhi.dialogue.llm.tool.mcp.device.DeviceMcpService;
import com.xiaozhi.dialogue.runtime.Persona;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BaiduImageSearchFunction implements ToolsGlobalRegistry.GlobalFunction {
    private static final String TOOL_NAME = "search_and_display_gif";
    private static final String TEMP_DIR = "/tmp/xiaozhi-images";
    private static final int MAX_IMAGE_SIZE = 500 * 1024; // 500KB
    private static final int MAX_CANDIDATES = 5;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Resource
    private DeviceMcpService deviceMcpService;

    @Resource
    private SessionManager sessionManager;

    @Resource
    private ServerAddressProvider serverAddressProvider;

    private final ToolCallback toolCallback = FunctionToolCallback
            .builder(TOOL_NAME, (Map<String, Object> params, ToolContext toolContext) -> {
                String query = (String) params.get("query");
                if (query == null || query.trim().isEmpty()) {
                    return "搜索关键词不能为空";
                }

                boolean gifOnly = Boolean.TRUE.equals(params.get("gif_only"));

                // 从 ToolContext 获取 session 和 device 信息
                String sessionId = (String) toolContext.getContext().get(Persona.TOOL_CONTEXT_SESSION_ID_KEY);
                String deviceId = (String) toolContext.getContext().get("deviceId");

                log.info("百度图片搜索: query={}, gif_only={}, sessionId={}, deviceId={}", query, gifOnly, sessionId, deviceId);

                try {
                    // 1. 搜索百度图片
                    List<ImageCandidate> candidates = searchBaiduImages(query, gifOnly);
                    if (candidates.isEmpty()) {
                        return gifOnly
                                ? "未找到与\"" + query + "\"相关的GIF图片"
                                : "未找到与\"" + query + "\"相关的图片";
                    }

                    // 2. 下载验证图片
                    Path downloadedPath = downloadAndValidate(candidates);
                    if (downloadedPath == null) {
                        return "找到了图片但下载失败，请稍后重试";
                    }

                    String fileName = downloadedPath.getFileName().toString();
                    String contentType = Files.probeContentType(downloadedPath);

                    // 3. 构造本地 URL
                    String localUrl = serverAddressProvider.getServerAddress() + "/api/images/temp/" + fileName;
                    log.info("本地图片URL: {}", localUrl);

                    // 4. 调用设备 MCP 工具显示图片
                    Map<String, Object> result = deviceMcpService.callDeviceTool(
                            deviceId,
                            "self.screen.display_gif",
                            Map.of("url", localUrl)
                    );

                    log.info("设备 MCP 调用结果: {}", result);

                    // 5. 返回成功信息给 LLM
                    ImageCandidate used = candidates.stream()
                            .filter(c -> c.matchesFile(downloadedPath))
                            .findFirst()
                            .orElse(null);

                    if (used != null) {
                        return String.format("已成功在设备上显示图片。关键词: %s, 类型: %s, 尺寸: %dx%d",
                                query, contentType != null ? contentType : "unknown", used.width, used.height);
                    }
                    return "已成功在设备上显示图片: " + query;

                } catch (Exception e) {
                    log.error("图片搜索显示失败: query={}", query, e);
                    return "图片搜索失败: " + e.getMessage();
                }
            })
            .description("搜索网络图片并在设备屏幕上显示。当用户要求查看某个主题的图片、GIF动图时调用此工具。"
                    + "工具会自动搜索百度图片、下载到本地、并在ESP32设备屏幕上显示。"
                    + "参数 gif_only 为 true 时只搜索GIF动图。")
            .inputSchema("""
                    {
                        "type": "object",
                        "properties": {
                            "query": {
                                "type": "string",
                                "description": "搜索关键词，如'猫咪 GIF'、'搞笑表情包'、'风景图片'"
                            },
                            "gif_only": {
                                "type": "boolean",
                                "description": "是否只搜索GIF动图，默认为false。当用户明确要求GIF时设为true。",
                                "default": false
                            }
                        },
                        "required": ["query"]
                    }
                    """)
            .inputType(Map.class)
            .toolCallResultConverter(ToolCallStringResultConverter.INSTANCE)
            .build();

    @Override
    public ToolCallback getFunctionCallTool(ToolSession toolSession) {
        return toolCallback;
    }

    @Override
    public String getToolName() {
        return TOOL_NAME;
    }

    @Override
    public String getToolDescription() {
        return "百度图片搜索与设备显示";
    }

    /**
     * 调用百度图片搜索 API
     */
    private List<ImageCandidate> searchBaiduImages(String query, boolean gifOnly) {
        List<ImageCandidate> candidates = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = String.format(
                    "https://image.baidu.com/search/acjson?tn=resultjson_com&word=%s&pn=0&rn=30&ie=utf-8",
                    encodedQuery
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                    .header("Accept", "application/json, text/plain, */*")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("百度图片搜索失败: statusCode={}", response.statusCode());
                return candidates;
            }

            JsonNode root = MAPPER.readTree(response.body());
            JsonNode dataArray = root.get("data");
            if (dataArray == null || !dataArray.isArray()) {
                return candidates;
            }

            for (JsonNode item : dataArray) {
                if (item == null || item.isNull()) continue;

                String thumbURL = getTextOrNull(item, "thumbURL");
                String type = getTextOrNull(item, "type");
                int width = getIntOrZero(item, "width");
                int height = getIntOrZero(item, "height");

                // 跳过没有 URL 的结果
                if (thumbURL == null || thumbURL.isEmpty()) continue;

                // GIF 筛选
                boolean isGif = "gif".equalsIgnoreCase(type) || thumbURL.toLowerCase().endsWith(".gif");
                if (gifOnly && !isGif) continue;

                // 尺寸筛选 (优先选择 ≤ 500px 的图片)
                if (width > 0 && height > 0 && (width > 800 || height > 800)) continue;

                candidates.add(new ImageCandidate(thumbURL, type, width, height, isGif));
                if (candidates.size() >= MAX_CANDIDATES * 2) break; // 多收集一些用于下载重试
            }

            log.info("百度图片搜索 '{}' 返回 {} 个候选", query, candidates.size());
        } catch (Exception e) {
            log.error("百度图片搜索异常: query={}", query, e);
        }
        return candidates;
    }

    /**
     * 下载并验证图片
     */
    private Path downloadAndValidate(List<ImageCandidate> candidates) {
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
        } catch (Exception e) {
            log.error("创建临时目录失败: {}", TEMP_DIR, e);
            return null;
        }

        int tried = 0;
        for (ImageCandidate candidate : candidates) {
            if (tried >= MAX_CANDIDATES) break;
            tried++;

            try {
                String imageUrl = candidate.url;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(imageUrl))
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                        .timeout(Duration.ofSeconds(8))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    log.debug("下载图片失败 ({}): statusCode={}", imageUrl, response.statusCode());
                    continue;
                }

                byte[] data = response.body();
                if (data == null || data.length == 0 || data.length > MAX_IMAGE_SIZE) {
                    log.debug("图片大小不合适: {} bytes, url={}", data != null ? data.length : 0, imageUrl);
                    continue;
                }

                // 确定文件扩展名
                String contentType = response.headers().firstValue("content-type").orElse("");
                String ext = determineExtension(contentType, candidate);

                // 生成唯一文件名
                String fileName = System.currentTimeMillis() + "_" + tried + ext;
                Path filePath = Paths.get(TEMP_DIR, fileName);

                Files.write(filePath, data);

                log.info("图片下载成功: {} -> {} ({} bytes)", imageUrl, filePath, data.length);
                candidate.downloadedPath = filePath;
                return filePath;

            } catch (Exception e) {
                log.debug("下载图片异常: {}", candidate.url, e);
            }
        }

        return null;
    }

    private String determineExtension(String contentType, ImageCandidate candidate) {
        if (contentType != null) {
            if (contentType.contains("gif")) return ".gif";
            if (contentType.contains("jpeg") || contentType.contains("jpg")) return ".jpg";
            if (contentType.contains("png")) return ".png";
        }
        if (candidate.isGif) return ".gif";
        return ".jpg";
    }

    private String getTextOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asText() : null;
    }

    private int getIntOrZero(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value != null && !value.isNull()) ? value.asInt(0) : 0;
    }

    private static class ImageCandidate {
        final String url;
        final String type;
        final int width;
        final int height;
        final boolean isGif;
        Path downloadedPath;

        ImageCandidate(String url, String type, int width, int height, boolean isGif) {
            this.url = url;
            this.type = type;
            this.width = width;
            this.height = height;
            this.isGif = isGif;
        }

        boolean matchesFile(Path file) {
            return downloadedPath != null && downloadedPath.equals(file);
        }
    }
}
