package com.example.demo.util;

import com.example.demo.entity.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员密码工具类测试
 */
@SpringBootTest
class AdminPasswordUtilTest {

    @Test
    void testEncryptPassword() {
        Admin admin = new Admin();
        admin.setPassword("123456");
        
        Admin encryptedAdmin = AdminPasswordUtil.encryptPassword(admin);
        
        assertNotNull(encryptedAdmin.getPassword());
        assertNotEquals("123456", encryptedAdmin.getPassword());
        assertEquals(32, encryptedAdmin.getPassword().length()); // MD5加密后长度为32
        assertTrue(encryptedAdmin.getPassword().matches("[0-9a-fA-F]+")); // 应该是十六进制字符
    }

    @Test
    void testVerifyPassword() {
        String rawPassword = "123456";
        String encryptedPassword = "e10adc3949ba59abbe56e057f20f883e"; // 123456的MD5值
        
        assertTrue(AdminPasswordUtil.verifyPassword(rawPassword, encryptedPassword));
        assertFalse(AdminPasswordUtil.verifyPassword("wrongpassword", encryptedPassword));
        assertFalse(AdminPasswordUtil.verifyPassword(null, encryptedPassword));
        assertFalse(AdminPasswordUtil.verifyPassword(rawPassword, null));
    }

    @Test
    void testIsEncrypted() {
        // 测试已加密的密码
        assertTrue(AdminPasswordUtil.isEncrypted("e10adc3949ba59abbe56e057f20f883e")); // 123456的MD5
        assertTrue(AdminPasswordUtil.isEncrypted("5d41402abc4b2a76b9719d911017c592")); // hello的MD5
        
        // 测试未加密的密码
        assertFalse(AdminPasswordUtil.isEncrypted("123456"));
        assertFalse(AdminPasswordUtil.isEncrypted("password"));
        assertFalse(AdminPasswordUtil.isEncrypted(""));
        assertFalse(AdminPasswordUtil.isEncrypted(null));
        
        // 测试长度不对的情况
        assertFalse(AdminPasswordUtil.isEncrypted("abc123")); // 长度不是32
        assertFalse(AdminPasswordUtil.isEncrypted("e10adc3949ba59abbe56e057f20f883e1")); // 长度超过32
    }

    @Test
    void testEncryptEmptyPassword() {
        Admin admin = new Admin();
        admin.setPassword("");
        
        Admin result = AdminPasswordUtil.encryptPassword(admin);
        assertEquals("", result.getPassword()); // 空密码应该保持为空
        
        admin.setPassword(null);
        result = AdminPasswordUtil.encryptPassword(admin);
        assertNull(result.getPassword()); // null密码应该保持为null
    }
}