package com.xiaozhi.dialogue.llm.tool.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaozhi.ai.llm.tool.ToolCallStringResultConverter;
import com.xiaozhi.ai.tool.ToolsGlobalRegistry;
import com.xiaozhi.ai.tool.session.ToolSession;
import com.xiaozhi.communication.common.ChatSession;
import com.xiaozhi.communication.common.SessionManager;
import com.xiaozhi.device.domain.repository.DeviceRepository;
import com.xiaozhi.dialogue.device.DeviceHttpClient;
import com.xiaozhi.dialogue.runtime.Persona;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BaiduImageSearchFunction implements ToolsGlobalRegistry.GlobalFunction {
    private static final String TOOL_NAME = "search_and_display_gif";
    private static final String SD_IMAGE_DIR = "images";
    private static final int MAX_IMAGE_SIZE = 500 * 1024; // 500KB
    private static final int MAX_CANDIDATES = 5;
    private static final int DISPLAY_DURATION_MS = 5000;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Resource
    private DeviceHttpClient deviceHttpClient;

    @Resource
    private SessionManager sessionManager;

    @Resource
    private DeviceRepository deviceRepository;

    private final ToolCallback toolCallback = FunctionToolCallback
            .builder(TOOL_NAME, (Map<String, Object> params, ToolContext toolContext) -> {
                String query = (String) params.get("query");
                if (query == null || query.trim().isEmpty()) {
                    return "搜索关键词不能为空";
                }

                boolean gifOnly = Boolean.TRUE.equals(params.get("gif_only"));

                String sessionId = (String) toolContext.getContext().get(Persona.TOOL_CONTEXT_SESSION_ID_KEY);
                String deviceId = (String) toolContext.getContext().get("deviceId");

                log.info("百度图片搜索: query={}, gif_only={}, sessionId={}, deviceId={}",
                        query, gifOnly, sessionId, deviceId);

                try {
                    String deviceIp = resolveDeviceIp(sessionId, deviceId);
                    if (!StringUtils.hasText(deviceIp)) {
                        return "无法获取设备 IP，请确认设备在线且已上报 IP";
                    }

                    List<ImageCandidate> candidates = searchBaiduImages(query, gifOnly);
                    if (candidates.isEmpty()) {
                        return gifOnly
                                ? "未找到与\"" + query + "\"相关的GIF图片"
                                : "未找到与\"" + query + "\"相关的图片";
                    }

                    DownloadedImage downloaded = downloadAndValidate(candidates);
                    if (downloaded == null) {
                        return "找到了图片但下载失败，请稍后重试";
                    }

                    String sdPath = SD_IMAGE_DIR + "/search_"
                            + System.currentTimeMillis() + downloaded.extension;

                    JsonNode uploadResult = deviceHttpClient.uploadToSdCard(
                            deviceIp,
                            sdPath,
                            downloaded.data,
                            true,
                            DISPLAY_DURATION_MS,
                            downloaded.isGif
                    );

                    log.info("设备 SD 卡上传并显示成功: path={}, response={}", sdPath, uploadResult);

                    ImageCandidate used = downloaded.candidate;
                    return String.format("已成功在设备上显示图片。关键词: %s, 路径: /sdcard/%s, 类型: %s, 尺寸: %dx%d",
                            query, sdPath, downloaded.contentType, used.width, used.height);

                } catch (Exception e) {
                    log.error("图片搜索显示失败: query={}", query, e);
                    return "图片搜索失败: " + e.getMessage();
                }
            })
            .toolMetadata(ToolMetadata.builder().returnDirect(false).build())
            .description("搜索网络图片并在设备屏幕上显示。当用户要求查看某个主题的图片、GIF动图时调用此工具。"
                    + "工具会自动搜索百度图片、上传到设备SD卡、并在ESP32贾维斯屏幕上显示。"
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

    private String resolveDeviceIp(String sessionId, String deviceId) {
        if (StringUtils.hasText(sessionId)) {
            ChatSession chatSession = sessionManager.getSession(sessionId);
            if (chatSession != null && chatSession.getDevice() != null) {
                String ip = chatSession.getDevice().getIp();
                if (StringUtils.hasText(ip)) {
                    return ip;
                }
            }
        }
        if (StringUtils.hasText(deviceId)) {
            return deviceRepository.findById(deviceId)
                    .map(device -> device.getIp())
                    .filter(StringUtils::hasText)
                    .orElse(null);
        }
        return null;
    }

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

                if (thumbURL == null || thumbURL.isEmpty()) continue;

                boolean isGif = "gif".equalsIgnoreCase(type) || thumbURL.toLowerCase().endsWith(".gif");
                if (gifOnly && !isGif) continue;

                if (width > 0 && height > 0 && (width > 500 || height > 500)) continue;

                candidates.add(new ImageCandidate(thumbURL, type, width, height, isGif));
                if (candidates.size() >= MAX_CANDIDATES * 2) break;
            }

            log.info("百度图片搜索 '{}' 返回 {} 个候选", query, candidates.size());
        } catch (Exception e) {
            log.error("百度图片搜索异常: query={}", query, e);
        }
        return candidates;
    }

    private DownloadedImage downloadAndValidate(List<ImageCandidate> candidates) {
        int tried = 0;
        for (ImageCandidate candidate : candidates) {
            if (tried >= MAX_CANDIDATES) break;
            tried++;

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(candidate.url))
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                        .timeout(Duration.ofSeconds(8))
                        .GET()
                        .build();

                HttpResponse<byte[]> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    continue;
                }

                byte[] data = response.body();
                if (data == null || data.length == 0 || data.length > MAX_IMAGE_SIZE) {
                    continue;
                }

                String contentType = response.headers().firstValue("content-type").orElse("");
                if (!isImageContentType(contentType, candidate)) {
                    log.debug("跳过非图片内容: contentType={}, url={}", contentType, candidate.url);
                    continue;
                }

                String ext = determineExtension(contentType, candidate);
                log.info("图片下载成功: {} ({} bytes)", candidate.url, data.length);
                return new DownloadedImage(data, ext, contentType, candidate);

            } catch (Exception e) {
                log.debug("下载图片异常: {}", candidate.url, e);
            }
        }
        return null;
    }

    private boolean isImageContentType(String contentType, ImageCandidate candidate) {
        if (contentType != null && contentType.toLowerCase().startsWith("image/")) {
            return true;
        }
        if (contentType == null || contentType.isBlank()) {
            return candidate.isGif
                    || candidate.url.toLowerCase().matches(".*\\.(gif|jpe?g|png|webp)(\\?.*)?$");
        }
        return false;
    }

    private String determineExtension(String contentType, ImageCandidate candidate) {
        if (contentType != null) {
            if (contentType.contains("gif")) return ".gif";
            if (contentType.contains("jpeg") || contentType.contains("jpg")) return ".jpg";
            if (contentType.contains("png")) return ".png";
            if (contentType.contains("webp")) return ".webp";
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

        ImageCandidate(String url, String type, int width, int height, boolean isGif) {
            this.url = url;
            this.type = type;
            this.width = width;
            this.height = height;
            this.isGif = isGif;
        }
    }

    private static class DownloadedImage {
        final byte[] data;
        final String extension;
        final String contentType;
        final ImageCandidate candidate;
        final boolean isGif;

        DownloadedImage(byte[] data, String extension, String contentType, ImageCandidate candidate) {
            this.data = data;
            this.extension = extension;
            this.contentType = contentType;
            this.candidate = candidate;
            this.isGif = candidate.isGif || ".gif".equalsIgnoreCase(extension);
        }
    }
}
