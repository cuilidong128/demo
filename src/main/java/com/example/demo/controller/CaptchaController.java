package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CaptchaRequest;
import com.example.demo.dto.CaptchaResponse;
import com.example.demo.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取验证码图片
     */
    @GetMapping("/image")
    public ApiResponse<CaptchaResponse> getCaptchaImage() {
        try {
            CaptchaResponse response = captchaService.generateCaptcha();
            log.info("验证码生成成功: uuid={}", response.getUuid());
            return ApiResponse.success("验证码生成成功", response);
        } catch (Exception e) {
            log.error("验证码生成失败", e);
            return ApiResponse.error("验证码生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证验证码（用于前端预验证）
     */
    @PostMapping("/validate")
    public ApiResponse<Boolean> validateCaptcha(@Valid @RequestBody CaptchaRequest captchaRequest) {
        try {
            boolean isValid = captchaService.validateCaptcha(captchaRequest.getUuid(), captchaRequest.getCode());

            if (isValid) {
                // 验证成功后立即删除验证码
                captchaService.removeCaptcha(captchaRequest.getUuid());
                log.info("验证码验证成功: uuid={}", captchaRequest.getUuid());
                return ApiResponse.success("验证码正确", true);
            } else {
                log.warn("验证码验证失败: uuid={}", captchaRequest.getUuid());
                return ApiResponse.error(400, "验证码错误");
            }
        } catch (Exception e) {
            log.error("验证码验证异常", e);
            return ApiResponse.error("验证码验证失败: " + e.getMessage());
        }
    }
}
