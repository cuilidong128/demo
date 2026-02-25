package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {

    /**
     * 本地上传配置
     */
    private LocalConfig local;

    /**
     * 阿里云OSS配置
     */
    private OssConfig oss;

    @Data
    public static class LocalConfig {
        /**
         * 是否启用本地上传
         */
        private boolean enabled;

        /**
         * 上传路径（绝对路径）
         */
        private String path;

        /**
         * 访问URL前缀
         */
        private String urlPrefix;
    }

    @Data
    public static class OssConfig {
        /**
         * 是否启用OSS上传
         */
        private boolean enabled;

        /**
         * OSS端点
         */
        private String endpoint;

        /**
         * AccessKey ID
         */
        private String accessKeyId;

        /**
         * AccessKey Secret
         */
        private String accessKeySecret;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 访问URL前缀
         */
        private String urlPrefix;
    }
}
