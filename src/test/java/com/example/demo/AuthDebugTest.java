package com.example.demo;

import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.util.Md5Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthDebugTest {

    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private com.example.demo.security.UserDetailsServiceImpl userDetailsService;

    @Test
    public void testAdminData() {
        // 检查数据库中的管理员数据
        System.out.println("=== 检查管理员数据 ===");
        Admin admin = adminMapper.selectById(1);
        if (admin != null) {
            System.out.println("管理员ID: " + admin.getAdminId());
            System.out.println("管理员姓名: " + admin.getName());
            System.out.println("密码: " + admin.getPassword());
            System.out.println("组ID: " + admin.getGroupId());
            System.out.println("是否超级管理员: " + admin.getIsSuper());
            
            // 检查密码是否为MD5格式
            String password = admin.getPassword();
            if (password != null && password.length() == 32 && password.matches("[0-9a-fA-F]+")) {
                System.out.println("✓ 密码格式正确（MD5）");
            } else {
                System.out.println("✗ 密码格式不正确，可能是明文");
            }
        } else {
            System.out.println("未找到ID为1的管理员");
        }
    }

    @Test
    public void testLoadUserByUsername() {
        System.out.println("=== 测试用户加载 ===");
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
            System.out.println("用户名: " + userDetails.getUsername());
            System.out.println("密码: " + userDetails.getPassword());
            System.out.println("账号是否过期: " + userDetails.isAccountNonExpired());
            System.out.println("账号是否锁定: " + userDetails.isAccountNonLocked());
            System.out.println("凭证是否过期: " + userDetails.isCredentialsNonExpired());
            System.out.println("账号是否启用: " + userDetails.isEnabled());
            System.out.println("权限: " + userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            System.out.println("用户未找到: " + e.getMessage());
        }
    }

    @Test
    public void testMd5Encryption() {
        System.out.println("=== 测试MD5加密 ===");
        String rawPassword = "123456";
        String encrypted = Md5Util.encrypt(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("MD5加密后: " + encrypted);
        System.out.println("长度: " + encrypted.length());
        System.out.println("是否为十六进制: " + encrypted.matches("[0-9a-fA-F]+"));
    }
}