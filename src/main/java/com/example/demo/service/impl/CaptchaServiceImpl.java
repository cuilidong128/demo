package com.example.demo.service.impl;

import com.example.demo.dto.CaptchaResponse;
import com.example.demo.service.CaptchaService;
import com.google.code.kaptcha.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final Producer captchaProducer;
    
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${captcha.expire-time:300}")
    private Integer expireTime;

    @Override
    public CaptchaResponse generateCaptcha() {
        // 生成UUID作为验证码标识
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(captchaText);
        
        // 将图片转换为Base64编码
        String base64Image = imageToBase64(image);
        
        // 存储到Redis，设置过期时间
        redisTemplate.opsForValue().set("captcha:" + uuid, captchaText, expireTime, TimeUnit.SECONDS);
        
        log.info("生成验证码: uuid={}, text={}", uuid, captchaText);
        
        return CaptchaResponse.builder()
                .uuid(uuid)
                .image(base64Image)
                .build();
    }

    @Override
    public boolean validateCaptcha(String uuid, String code) {
        if (uuid == null || code == null) {
            return false;
        }
        
        // 从Redis获取验证码
        String storedCode = redisTemplate.opsForValue().get("captcha:" + uuid);
        
        if (storedCode == null) {
            log.warn("验证码不存在或已过期: uuid={}", uuid);
            return false;
        }
        
        // 比较验证码（忽略大小写）
        boolean isValid = storedCode.equalsIgnoreCase(code.trim());
        
        log.info("验证验证码: uuid={}, input={}, stored={}, result={}", 
                uuid, code, storedCode, isValid);
        
        return isValid;
    }

    @Override
    public void removeCaptcha(String uuid) {
        if (uuid != null) {
            redisTemplate.delete("captcha:" + uuid);
            log.info("删除验证码: uuid={}", uuid);
        }
    }

    /**
     * 将BufferedImage转换为Base64编码的字符串
     */
    private String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64Utils.encodeToString(bytes);
        } catch (IOException e) {
            log.error("图片转Base64失败", e);
            throw new RuntimeException("验证码生成失败");
        }
    }
}