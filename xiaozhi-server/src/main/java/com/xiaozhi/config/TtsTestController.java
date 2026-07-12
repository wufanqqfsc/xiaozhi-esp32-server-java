package com.xiaozhi.config;

import com.xiaozhi.server.web.BaseController;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.xiaozhi.ai.tts.TtsServiceFactory;
import com.xiaozhi.common.model.bo.ConfigBO;
import com.xiaozhi.common.web.ApiResponse;
import com.xiaozhi.config.service.ConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/tts")
@Tag(name = "TTS 测试", description = "TTS 语音合成测试接口")
public class TtsTestController extends BaseController {

    @Resource
    private TtsServiceFactory ttsServiceFactory;

    @Resource
    private ConfigService configService;

    @PostMapping("/test")
    @SaCheckPermission("system:config")
    @Operation(summary = "TTS 语音合成测试", description = "根据配置ID或默认配置合成语音并返回音频文件")
    public void testTts(
            @RequestParam(required = false) Integer configId,
            @RequestParam(defaultValue = "zh-CN-XiaoyiNeural") String voiceName,
            @RequestParam(defaultValue = "1.0") Double speed,
            @RequestParam(defaultValue = "1.0") Double pitch,
            @RequestParam String text,
            HttpServletResponse response
    ) {
        try {
            ConfigBO config;

            if (configId != null) {
                config = configService.getBO(configId);
                if (config == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(ApiResponse.error("配置不存在").toString());
                    return;
                }
            } else {
                config = configService.getDefaultConfig("tts");
            }

            var ttsService = ttsServiceFactory.getTtsService(config, voiceName, pitch, speed);
            Path audioPath = ttsService.textToSpeech(text);

            if (audioPath == null || !Files.exists(audioPath)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("语音合成失败");
                return;
            }

            response.setContentType("audio/mpeg");
            response.setHeader("Content-Disposition", "attachment; filename=\"tts_test.mp3\"");
            response.setContentLengthLong(Files.size(audioPath));

            try (InputStream in = Files.newInputStream(audioPath);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }

            try {
                Files.deleteIfExists(audioPath);
            } catch (IOException e) {
                log.warn("删除临时音频文件失败", e);
            }

        } catch (Exception e) {
            log.error("TTS 测试失败", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(ApiResponse.error("TTS 合成失败: " + e.getMessage()).toString());
            } catch (IOException ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }
}
