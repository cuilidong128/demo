package com.example.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 验证码验证请求DTO
 */
@Data
public class CaptchaRequest {

    /**
     * 验证码UUID
     */
    @NotBlank(message = "验证码UUID不能为空")
    private String uuid;

    /**
     * 用户输入的验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}