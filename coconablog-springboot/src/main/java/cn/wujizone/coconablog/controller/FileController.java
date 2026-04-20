package cn.wujizone.coconablog.controller;

import cn.wujizone.coconablog.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload/image")
    public Result<String> uploadImage(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file) {
        log.info("上传图片, userId={}, fileName={}", userId, file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片文件");
        }

        // 限制大小 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过10MB");
        }

        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir, "images").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // 生成唯一文件名，保留原始扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 返回可访问的 URL 路径
            String url = "/uploads/images/" + filename;
            return Result.success(url);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
}
