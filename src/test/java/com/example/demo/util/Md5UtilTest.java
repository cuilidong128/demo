package com.example.demo.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MD5工具类测试
 */
@SpringBootTest
public class Md5UtilTest {

    @Test
    public void testEncrypt() {
        // 测试基本加密功能
        String input = "123456";
        String encrypted = Md5Util.encrypt(input);
        
        // MD5应该是32位十六进制字符串
        assertEquals(32, encrypted.length());
        assertEquals("e10adc3949ba59abbe56e057f20f883e", encrypted); // 已知的MD5值
        
        // 测试空字符串
        assertEquals("", Md5Util.encrypt(""));
        assertEquals("", Md5Util.encrypt(null));
        
        // 测试不同输入产生不同输出
        assertNotEquals(Md5Util.encrypt("password1"), Md5Util.encrypt("password2"));
    }

    @Test
    public void testVerify() {
        String plainText = "hello123";
        String encrypted = Md5Util.encrypt(plainText);
        
        // 验证正确的明文和密文
        assertTrue(Md5Util.verify(plainText, encrypted));
        
        // 验证错误的明文
        assertFalse(Md5Util.verify("wrong_password", encrypted));
        
        // 验证null情况
        assertFalse(Md5Util.verify(null, encrypted));
        assertFalse(Md5Util.verify(plainText, null));
    }

    @Test
    public void testGenerateSalt() {
        // 测试生成指定长度的盐值
        String salt1 = Md5Util.generateSalt(8);
        String salt2 = Md5Util.generateSalt(16);
        
        assertEquals(8, salt1.length());
        assertEquals(16, salt2.length());
        
        // 验证两次生成的盐值不同
        assertNotEquals(salt1, salt2);
        
        // 验证盐值只包含字母和数字
        assertTrue(salt1.matches("[A-Za-z0-9]+"));
        assertTrue(salt2.matches("[A-Za-z0-9]+"));
    }

    @Test
    public void testEncryptWithSalt() {
        String input = "mypassword";
        String salt = "random_salt_123";
        
        String encrypted = Md5Util.encryptWithSalt(input, salt);
        String expected = Md5Util.encrypt(input + salt);
        
        assertEquals(expected, encrypted);
        
        // 验证带盐值的验证功能
        assertTrue(Md5Util.verifyWithSalt(input, encrypted, salt));
        assertFalse(Md5Util.verifyWithSalt("wrong_password", encrypted, salt));
    }

    @Test
    public void testDifferentInputsProduceDifferentHashes() {
        // 测试不同的输入应该产生不同的哈希值
        String[] inputs = {"admin123", "user456", "guest789", "root"};
        
        for (int i = 0; i < inputs.length; i++) {
            for (int j = i + 1; j < inputs.length; j++) {
                assertNotEquals(
                    Md5Util.encrypt(inputs[i]), 
                    Md5Util.encrypt(inputs[j]),
                    "Different inputs should produce different hashes"
                );
            }
        }
    }
}