package com.example.demo.service;

import com.example.demo.dto.CaptchaResponse;

/**
 * 验证码服务接口
 */
public interface CaptchaService {

    /**
     * 生成验证码
     * @return 验证码响应对象
     */
    CaptchaResponse generateCaptcha();

    /**
     * 验证验证码
     * @param uuid 验证码UUID
     * @param code 用户输入的验证码
     * @return 验证结果
     */
    boolean validateCaptcha(String uuid, String code);

    /**
     * 删除验证码（验证后立即删除）
     * @param uuid 验证码UUID
     */
    void removeCaptcha(String uuid);
}