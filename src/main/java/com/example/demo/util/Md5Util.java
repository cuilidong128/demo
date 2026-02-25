package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
@Slf4j
@Component
public class Md5Util {

    /**
     * 对字符串进行MD5加密
     * @param input 待加密的字符串
     * @return MD5加密后的字符串（32位小写）
     */
    public static String encrypt(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5加密失败: {}", e.getMessage());
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 验证明文与MD5密文是否匹配
     * @param plainText 明文
     * @param encryptedText MD5密文
     * @return 是否匹配
     */
    public static boolean verify(String plainText, String encryptedText) {
        if (plainText == null || encryptedText == null) {
            return false;
        }
        return encrypt(plainText).equals(encryptedText);
    }

    /**
     * 生成随机盐值
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }

    /**
     * 带盐值的MD5加密
     * @param input 待加密字符串
     * @param salt 盐值
     * @return 加密后的字符串
     */
    public static String encryptWithSalt(String input, String salt) {
        return encrypt(input + salt);
    }

    /**
     * 验证带盐值的MD5密文
     * @param plainText 明文
     * @param encryptedText 密文
     * @param salt 盐值
     * @return 是否匹配
     */
    public static boolean verifyWithSalt(String plainText, String encryptedText, String salt) {
        return encryptWithSalt(plainText, salt).equals(encryptedText);
    }
}