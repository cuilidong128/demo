package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseTableAnalyzer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void analyzeTable(String tableName) {
        try {
            // 获取表结构信息
            String sql = "DESCRIBE " + tableName;
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
            
            System.out.println("表 " + tableName + " 的结构信息：");
            System.out.println("----------------------------------------");
            for (Map<String, Object> column : columns) {
                System.out.println("字段名: " + column.get("Field"));
                System.out.println("类型: " + column.get("Type"));
                System.out.println("是否为空: " + column.get("Null"));
                System.out.println("键: " + column.get("Key"));
                System.out.println("默认值: " + column.get("Default"));
                System.out.println("额外信息: " + column.get("Extra"));
                System.out.println("----------------------------------------");
            }
            
            // 获取表的创建语句
            String createSql = "SHOW CREATE TABLE " + tableName;
            List<Map<String, Object>> createInfo = jdbcTemplate.queryForList(createSql);
            if (!createInfo.isEmpty()) {
                System.out.println("表创建语句：");
                System.out.println(createInfo.get(0).get("Create Table"));
            }
            
        } catch (Exception e) {
            System.err.println("分析表结构时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}