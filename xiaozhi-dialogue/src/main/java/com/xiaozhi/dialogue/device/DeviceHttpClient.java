package com.xiaozhi.dialogue.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 通过设备本地 HTTP 服务（默认 :8080）与 ESP32 交互。
 */
@Slf4j
@Component
public class DeviceHttpClient {

    private static final int DEVICE_HTTP_PORT = 8080;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * 上传文件到设备 SD 卡，可选上传后自动显示。
     *
     * @param deviceIp     设备局域网 IP
     * @param sdPath       SD 卡相对路径，如 images/search_123.gif
     * @param data         文件二进制内容
     * @param autoDisplay  上传成功后是否触发显示
     * @param durationMs   显示时长（毫秒），0 表示使用设备默认值
     * @param loop         GIF 是否循环播放
     * @return 设备返回的 JSON 响应
     */
    public JsonNode uploadToSdCard(String deviceIp, String sdPath, byte[] data,
                                   boolean autoDisplay, int durationMs, boolean loop) throws Exception {
        if (deviceIp == null || deviceIp.isBlank()) {
            throw new IllegalArgumentException("设备 IP 为空");
        }
        if (sdPath == null || sdPath.isBlank()) {
            throw new IllegalArgumentException("SD 卡路径为空");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("上传内容为空");
        }

        String encodedPath = URLEncoder.encode(sdPath, StandardCharsets.UTF_8)
                .replace("+", "%20");
        StringBuilder url = new StringBuilder()
                .append("http://")
                .append(deviceIp)
                .append(":")
                .append(DEVICE_HTTP_PORT)
                .append("/api/sdcard/files/")
                .append(encodedPath);
        if (autoDisplay) {
            url.append("?display=1")
                    .append("&duration_ms=").append(Math.max(durationMs, 0))
                    .append("&loop=").append(loop ? "1" : "0");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();

        log.info("上传图片到设备 SD 卡: url={}, size={} bytes", url, data.length);
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("设备上传失败: HTTP " + response.statusCode()
                    + ", body=" + response.body());
        }

        JsonNode root = MAPPER.readTree(response.body());
        if (!root.path("ok").asBoolean(false)) {
            String error = root.path("error").asText("unknown error");
            throw new IllegalStateException("设备上传失败: " + error);
        }
        if (autoDisplay && root.has("displayed") && !root.path("displayed").asBoolean(true)) {
            String displayError = root.path("display_error").asText("display failed");
            throw new IllegalStateException("图片已上传但显示失败: " + displayError);
        }
        return root;
    }
}
