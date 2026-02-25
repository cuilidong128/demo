package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminService;
import com.example.demo.util.AdminPasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    
    @Override
    public boolean save(Admin entity) {
        // 保存前对密码进行加密
        if (entity != null && entity.getPassword() != null) {
            // 只有当密码未加密时才进行加密
            if (!AdminPasswordUtil.isEncrypted(entity.getPassword())) {
                AdminPasswordUtil.encryptPassword(entity);
            }
        }
        return super.save(entity);
    }
    
    @Override
    public boolean updateById(Admin entity) {
        // 更新时如果密码被修改，则进行加密
        if (entity != null && entity.getPassword() != null) {
            Admin existingAdmin = this.getById(entity.getAdminId());
            if (existingAdmin != null && !existingAdmin.getPassword().equals(entity.getPassword())) {
                // 密码被修改了，需要重新加密
                if (!AdminPasswordUtil.isEncrypted(entity.getPassword())) {
                    AdminPasswordUtil.encryptPassword(entity);
                }
            }
        }
        return super.updateById(entity);
    }
}