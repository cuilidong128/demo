package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionTest.class);

    @Override
    public void run(String... args) throws Exception {
        testDatabaseConnection();
    }

    private void testDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/crmeb?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
        String username = "root";
        String password = "aA123456";

        try {
            logger.info("正在测试数据库连接...");
            logger.info("URL: {}", url);
            logger.info("Username: {}", username);
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            
            if (connection.isValid(5)) { // 5秒超时
                logger.info("✅ 数据库连接测试成功！");
                connection.close();
            } else {
                logger.error("❌ 数据库连接测试失败！");
            }
        } catch (ClassNotFoundException e) {
            logger.error("❌ MySQL驱动未找到: {}", e.getMessage());
        } catch (SQLException e) {
            logger.error("❌ 数据库连接失败: {}", e.getMessage());
            logger.error("请检查以下配置:");
            logger.error("1. MySQL服务是否正在运行");
            logger.error("2. 数据库名称 'crmeb' 是否存在");
            logger.error("3. 用户名和密码是否正确");
            logger.error("4. 端口3306是否正确");
        }
    }
}