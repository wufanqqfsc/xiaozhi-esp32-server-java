package com.xiaozhi.communication.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/images")
@Tag(name = "临时图片服务", description = "提供搜索下载的临时图片访问端点")
public class TempImageController {

    private static final String TEMP_DIR = "/tmp/xiaozhi-images";

    @SaIgnore
    @GetMapping("/temp/{filename}")
    @Operation(summary = "获取临时图片", description = "根据文件名返回临时图片文件")
    public ResponseEntity<byte[]> getTempImage(@PathVariable String filename) {
        try {
            // 防止路径遍历攻击
            if (filename.contains("..") || filename.contains("/")) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = Paths.get(TEMP_DIR, filename);
            if (!Files.exists(filePath)) {
                log.warn("临时图片不存在: {}", filename);
                return ResponseEntity.notFound().build();
            }

            byte[] data = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);

            MediaType mediaType;
            if (contentType != null && contentType.startsWith("image/")) {
                mediaType = MediaType.parseMediaType(contentType);
            } else if (filename.endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (filename.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else {
                mediaType = MediaType.IMAGE_JPEG;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .body(data);
        } catch (Exception e) {
            log.error("读取临时图片失败: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
