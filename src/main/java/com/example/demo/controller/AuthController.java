package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.entity.Admin;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("收到登录请求: {}", loginRequest.getUsername());
        try {
            LoginResponse response = authService.login(loginRequest);
            log.info("登录成功: {}", loginRequest.getUsername());
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            return ApiResponse.error(401, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("收到刷新Token请求");
        try {
            LoginResponse response = authService.refreshToken(refreshTokenRequest);
            log.info("Token刷新成功");
            return ApiResponse.success("Token刷新成功", response);
        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            return ApiResponse.error(401, "Token刷新失败: " + e.getMessage());
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        log.info("收到登出请求");
        try {
            authService.logout();
            log.info("登出成功");
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            return ApiResponse.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<LoginResponse.UserInfo> getCurrentUserInfo() {
        log.info("获取当前用户信息");
        try {
            Admin admin = authService.getCurrentAdmin();
            if (admin == null) {
                return ApiResponse.error(401, "未认证");
            }

            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .adminId(admin.getAdminId())
                .name(admin.getName())
                .avatar(admin.getAvatar())
                .groupId(admin.getGroupId())
                .groupName(admin.getGroupName())
                .isSuper(admin.getIsSuper())
                .build();

            return ApiResponse.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
