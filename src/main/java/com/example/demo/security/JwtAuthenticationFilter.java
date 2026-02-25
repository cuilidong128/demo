package com.example.demo.security;

import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * 更新版本以提高稳定性和错误处理能力
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        log.debug("处理请求: {}", requestURI);
        
        // 跳过不需要认证的路径
        if (shouldNotFilter(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                log.debug("检测到JWT Token");
                
                if (jwtUtil.validateTokenFormat(jwt)) {
                    String username = jwtUtil.extractUsername(jwt);
                    
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        try {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            
                            if (jwtUtil.validateToken(jwt, userDetails)) {
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        userDetails, 
                                        null, 
                                        userDetails.getAuthorities()
                                    );
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug("成功设置用户认证信息: {}", username);
                            } else {
                                log.warn("Token验证失败: {}", username);
                            }
                        } catch (Exception userDetailsEx) {
                            log.error("加载用户详情失败: {}", userDetailsEx.getMessage());
                        }
                    }
                } else {
                    log.warn("无效的JWT格式");
                }
            } else {
                log.debug("未找到JWT Token");
            }
        } catch (Exception ex) {
            log.error("JWT认证过程中发生错误: {}", ex.getMessage(), ex);
            // 不中断请求处理，让后续的认证机制处理
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断是否应该跳过过滤
     */
    private boolean shouldNotFilter(String requestURI) {
        return requestURI.startsWith("/api/auth/") ||
               requestURI.startsWith("/swagger-ui/") ||
               requestURI.startsWith("/v3/api-docs/") ||
               requestURI.startsWith("/webjars/") ||
               requestURI.equals("/favicon.ico") ||
               requestURI.startsWith("/actuator/");
    }
}