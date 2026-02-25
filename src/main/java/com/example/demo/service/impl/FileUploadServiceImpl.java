package com.example.demo.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.example.demo.config.UploadConfig;
import com.example.demo.dto.FileUploadResult;
import com.example.demo.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 * 支持本地上传和阿里云OSS上传
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final UploadConfig uploadConfig;
    private OSS ossClient;

    /**
     * 上传类型常量
     */
    public static final String UPLOAD_TYPE_LOCAL = "local";
    public static final String UPLOAD_TYPE_OSS = "oss";

    @PostConstruct
    public void init() {
        // 初始化OSS客户端
        if (uploadConfig.getOss() != null && uploadConfig.getOss().isEnabled()) {
            try {
                ossClient = new OSSClientBuilder().build(
                        uploadConfig.getOss().getEndpoint(),
                        uploadConfig.getOss().getAccessKeyId(),
                        uploadConfig.getOss().getAccessKeySecret()
                );
                log.info("阿里云OSS客户端初始化成功");
            } catch (Exception e) {
                log.error("阿里云OSS客户端初始化失败: {}", e.getMessage());
            }
        }

        // 确保本地上传目录存在
        if (uploadConfig.getLocal() != null && uploadConfig.getLocal().isEnabled()) {
            try {
                Path uploadPath = Paths.get(uploadConfig.getLocal().getPath());
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    log.info("创建本地上传目录: {}", uploadPath.toAbsolutePath());
                }
            } catch (IOException e) {
                log.error("创建本地上传目录失败: {}", e.getMessage());
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("阿里云OSS客户端已关闭");
        }
    }

    @Override
    public FileUploadResult upload(MultipartFile file, String uploadType) {
        return upload(file, uploadType, null);
    }

    @Override
    public FileUploadResult upload(MultipartFile file) {
        return upload(file, UPLOAD_TYPE_LOCAL, null);
    }

    @Override
    public FileUploadResult upload(MultipartFile file, String uploadType, String directory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = generateFileName(originalFilename);

        try (InputStream inputStream = file.getInputStream()) {
            return upload(inputStream, uploadType, fileName, directory);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public FileUploadResult upload(InputStream inputStream, String uploadType, String fileName, String directory) {
        if (UPLOAD_TYPE_OSS.equals(uploadType)) {
            return uploadToOss(inputStream, fileName, directory);
        } else {
            return uploadToLocal(inputStream, fileName, directory);
        }
    }

    /**
     * 本地上传
     */
    private FileUploadResult uploadToLocal(InputStream inputStream, String fileName, String directory) {
        if (uploadConfig.getLocal() == null || !uploadConfig.getLocal().isEnabled()) {
            throw new RuntimeException("本地上传未启用");
        }

        try {
            // 构建上传路径（使用绝对路径）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = directory != null ? directory + "/" + datePath + "/" + fileName : datePath + "/" + fileName;
            Path targetPath = Paths.get(uploadConfig.getLocal().getPath(), directory != null ? directory + "/" + datePath : datePath);

            // 确保目录存在
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            // 保存文件
            Path targetFile = targetPath.resolve(fileName);
            Files.copy(inputStream, targetFile);

            // 构建完整URL
            String fullUrl = uploadConfig.getLocal().getUrlPrefix() + "/" + relativePath;
            
            log.info("本地上传成功: relativePath={}, fullUrl={}", relativePath, fullUrl);
            
            return FileUploadResult.builder()
                    .relativePath(relativePath)
                    .fullUrl(fullUrl)
                    .fileName(fileName)
                    .build();

        } catch (IOException e) {
            log.error("本地上传失败: {}", e.getMessage());
            throw new RuntimeException("本地上传失败", e);
        }
    }

    /**
     * OSS上传
     */
    private FileUploadResult uploadToOss(InputStream inputStream, String fileName, String directory) {
        if (uploadConfig.getOss() == null || !uploadConfig.getOss().isEnabled()) {
            throw new RuntimeException("阿里云OSS上传未启用");
        }

        if (ossClient == null) {
            throw new RuntimeException("阿里云OSS客户端未初始化");
        }

        try {
            // 构建OSS路径（相对路径）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = directory != null 
                    ? directory + "/" + datePath + "/" + fileName 
                    : datePath + "/" + fileName;

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    uploadConfig.getOss().getBucketName(),
                    relativePath,
                    inputStream
            );
            ossClient.putObject(putObjectRequest);

            // 构建完整URL
            String fullUrl = uploadConfig.getOss().getUrlPrefix() + "/" + relativePath;
            
            log.info("OSS上传成功: relativePath={}, fullUrl={}", relativePath, fullUrl);
            
            return FileUploadResult.builder()
                    .relativePath(relativePath)
                    .fullUrl(fullUrl)
                    .fileName(fileName)
                    .build();

        } catch (Exception e) {
            log.error("OSS上传失败: {}", e.getMessage());
            throw new RuntimeException("OSS上传失败", e);
        }
    }

    @Override
    public boolean delete(String fileUrl, String uploadType) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            if (UPLOAD_TYPE_OSS.equals(uploadType)) {
                return deleteFromOss(fileUrl);
            } else {
                return deleteFromLocal(fileUrl);
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除本地文件
     */
    private boolean deleteFromLocal(String fileUrl) {
        try {
            String urlPrefix = uploadConfig.getLocal().getUrlPrefix();
            String relativePath = fileUrl.replace(urlPrefix, "");
            Path filePath = Paths.get(uploadConfig.getLocal().getPath(), relativePath);

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除本地文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除OSS文件
     */
    private boolean deleteFromOss(String fileUrl) {
        try {
            String urlPrefix = uploadConfig.getOss().getUrlPrefix();
            String objectKey = fileUrl.replace(urlPrefix + "/", "");

            ossClient.deleteObject(uploadConfig.getOss().getBucketName(), objectKey);
            return true;
        } catch (Exception e) {
            log.error("删除OSS文件失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUploadType(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return UPLOAD_TYPE_LOCAL;
        }

        String ossUrlPrefix = uploadConfig.getOss().getUrlPrefix();
        if (ossUrlPrefix != null && fileUrl.startsWith(ossUrlPrefix)) {
            return UPLOAD_TYPE_OSS;
        }

        return UPLOAD_TYPE_LOCAL;
    }

    @Override
    public String getFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // 如果已经是完整URL，直接返回
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }

        // 根据配置判断是本地还是OSS
        String uploadType = getUploadType(relativePath);
        if (UPLOAD_TYPE_OSS.equals(uploadType)) {
            return uploadConfig.getOss().getUrlPrefix() + "/" + relativePath;
        } else {
            return uploadConfig.getLocal().getUrlPrefix() + "/" + relativePath;
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}
