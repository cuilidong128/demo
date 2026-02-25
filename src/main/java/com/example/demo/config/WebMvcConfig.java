package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 配置
 * 配置静态资源映射，支持本地上传文件的访问
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadConfig uploadConfig;

    @PostConstruct
    public void init() {
        log.info("WebMvcConfig 初始化完成");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地上传文件的静态资源映射
        if (uploadConfig.getLocal() != null && uploadConfig.getLocal().isEnabled()) {
            String uploadPath = uploadConfig.getLocal().getPath();
            
            // 确保路径是绝对路径
            Path path = Paths.get(uploadPath);
            String absolutePath = path.toAbsolutePath().toString();
            
            // 添加 file: 协议前缀
            String resourceLocation = "file:" + absolutePath + "/";
            
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(resourceLocation);
            
            log.info("配置静态资源映射: /uploads/** -> {}", resourceLocation);
        }
    }
}
