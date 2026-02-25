package com.example.demo.util;

import com.example.demo.entity.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 管理员密码处理工具类
 */
@Slf4j
@Component
public class AdminPasswordUtil {

    /**
     * 对管理员密码进行MD5加密
     * @param admin 管理员对象
     * @return 加密后的管理员对象
     */
    public static Admin encryptPassword(Admin admin) {
        if (admin != null && admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String originalPassword = admin.getPassword();
            String encryptedPassword = Md5Util.encrypt(originalPassword);
            admin.setPassword(encryptedPassword);
            log.info("管理员密码加密完成: {} -> {}", originalPassword, encryptedPassword);
        }
        return admin;
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        String encryptedRawPassword = Md5Util.encrypt(rawPassword);
        return encryptedRawPassword.equals(encryptedPassword);
    }

    /**
     * 检查密码是否已经加密
     * @param password 密码
     * @return 是否已加密（简单判断：长度为32且只包含十六进制字符）
     */
    public static boolean isEncrypted(String password) {
        if (password == null || password.length() != 32) {
            return false;
        }
        return password.matches("[0-9a-fA-F]+");
    }
}