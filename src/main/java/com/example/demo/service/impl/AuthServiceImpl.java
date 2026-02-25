package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.AuthService;
import com.example.demo.service.CaptchaService;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.Md5Util;
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
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminMapper adminMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("管理员登录请求: {}", loginRequest.getUsername());

        try {
            // 验证验证码
            if (!captchaService.validateCaptcha(loginRequest.getCaptchaUuid(), loginRequest.getCaptchaCode())) {
                throw new BadCredentialsException("验证码错误");
            }

            // 验证成功后删除验证码
            captchaService.removeCaptcha(loginRequest.getCaptchaUuid());


            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 生成Token
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 获取管理员信息
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", loginRequest.getUsername());
            Admin admin = adminMapper.selectOne(queryWrapper);

            if (admin == null) {
                throw new BadCredentialsException("管理员不存在");
            }

            log.info("管理员登录成功: {}", admin.getName());

            // 构建响应
            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.extractExpiration(accessToken).getTime() - System.currentTimeMillis())
                .userInfo(LoginResponse.UserInfo.builder()
                    .adminId(admin.getAdminId())
                    .name(admin.getName())
                    .avatar(admin.getAvatar())
                    .groupId(admin.getGroupId())
                    .groupName(admin.getGroupName())
                    .isSuper(admin.getIsSuper())
                    .build())
                .build();

        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("刷新Token请求");

        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();

            // 验证Refresh Token
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("无效的Refresh Token");
            }

            String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken);

            // 加载用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 验证Token有效性
            if (!jwtUtil.validateToken(refreshToken, userDetails)) {
                throw new BadCredentialsException("Refresh Token已过期");
            }

            // 生成新的Access Token
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            // 获取管理员信息
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", username);
            Admin admin = adminMapper.selectOne(queryWrapper);

            log.info("Token刷新成功: {}", username);

            // 构建响应（注意：这里不重新生成Refresh Token）
            return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // 使用原来的Refresh Token
                .tokenType("Bearer")
                .expiresIn(jwtUtil.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis())
                .userInfo(LoginResponse.UserInfo.builder()
                    .adminId(admin.getAdminId())
                    .name(admin.getName())
                    .avatar(admin.getAvatar())
                    .groupId(admin.getGroupId())
                    .groupName(admin.getGroupName())
                    .isSuper(admin.getIsSuper())
                    .build())
                .build();

        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            throw new BadCredentialsException("刷新Token失败: " + e.getMessage());
        }
    }

    @Override
    public void logout() {
        log.info("用户登出");
        // 清除安全上下文
        SecurityContextHolder.clearContext();
        // 在实际应用中，可能还需要将Token加入黑名单
    }

    @Override
    public Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", username);
            return adminMapper.selectOne(queryWrapper);
        }
        return null;
    }
}
