package com.example.demo.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户详情服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminMapper adminMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("正在加载用户详情: {}", username);

        // 根据用户名查询管理员
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        Admin admin = adminMapper.selectOne(queryWrapper);

        if (admin == null) {
            log.warn("未找到用户: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        log.info("找到用户: {}, 超级管理员: {}", username, admin.getIsSuper());

        // 构建权限列表
        Collection<GrantedAuthority> authorities = getAuthorities(admin);

        // 返回Spring Security的User对象
        // 注意：确保数据库中的密码已经是MD5加密过的
        return User.builder()
                .username(admin.getName())
                .password(admin.getPassword()) // 数据库中存储的应该是MD5加密后的密码
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false) // 根据groupId判断是否锁定
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * 根据管理员信息获取权限
     */
    private Collection<GrantedAuthority> getAuthorities(Admin admin) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 添加基础角色
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // 如果是超级管理员，添加超级管理员权限
        if (admin.getIsSuper() != null && admin.getIsSuper() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
        }

        // 可以根据groupId或其他条件添加更多权限

        log.debug("用户 {} 的权限: {}", admin.getName(), authorities);
        return authorities;
    }

    /**
     * 根据管理员ID加载用户详情（额外方法）
     */
    public UserDetails loadUserById(Integer adminId) throws UsernameNotFoundException {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new UsernameNotFoundException("管理员不存在: " + adminId);
        }
        return loadUserByUsername(admin.getName());
    }
}
