package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 管理员登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 刷新Token
     */
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    
    /**
     * 登出
     */
    void logout();
    
    /**
     * 获取当前认证用户信息
     */
    Admin getCurrentAdmin();
}