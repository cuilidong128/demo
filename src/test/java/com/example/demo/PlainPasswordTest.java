package com.example.demo;

import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlainPasswordTest {

    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testPlainPasswordMatching() {
        System.out.println("=== 测试明文密码匹配 ===");
        
        // 测试PasswordEncoder是否正确处理明文密码
        String plainPassword = "123456";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        
        System.out.println("原始密码: " + plainPassword);
        System.out.println("编码后密码: " + encodedPassword);
        System.out.println("是否匹配: " + passwordEncoder.matches(plainPassword, encodedPassword));
        
        // 验证数据库中的管理员密码
        Admin admin = adminMapper.selectById(1);
        if (admin != null) {
            System.out.println("\n=== 数据库管理员信息 ===");
            System.out.println("管理员ID: " + admin.getAdminId());
            System.out.println("管理员姓名: " + admin.getName());
            System.out.println("数据库密码: " + admin.getPassword());
            System.out.println("组ID: " + admin.getGroupId());
            
            // 测试密码匹配
            boolean passwordMatch = passwordEncoder.matches("123456", admin.getPassword());
            System.out.println("密码'123456'是否匹配: " + passwordMatch);
        }
    }
}