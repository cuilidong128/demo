package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.AdminGroupPermission;
import com.example.demo.mapper.AdminGroupPermissionMapper;
import com.example.demo.service.AdminGroupPermissionService;
import org.springframework.stereotype.Service;

/**
 * 管理员组权限服务实现类
 */
@Service
public class AdminGroupPermissionServiceImpl extends ServiceImpl<AdminGroupPermissionMapper, AdminGroupPermission> implements AdminGroupPermissionService {
}