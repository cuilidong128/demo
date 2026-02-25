package com.example.demo.util;

import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 管理员数据检查工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataChecker implements CommandLineRunner {

    private final AdminMapper adminMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始检查管理员数据 ===");
        
        // 查询所有管理员
        List<Admin> admins = adminMapper.selectList(null);
        log.info("数据库中共有 {} 个管理员", admins.size());
        
        for (Admin admin : admins) {
            log.info("管理员信息 - ID: {}, 姓名: {}, 密码: {}, 头像: {}, 组ID: {}, 是否超级管理员: {}", 
                admin.getAdminId(), 
                admin.getName(), 
                admin.getPassword(),
                admin.getAvatar(),
                admin.getGroupId(),
                admin.getIsSuper()
            );
        }
        
        log.info("=== 管理员数据检查完成 ===");
    }
}