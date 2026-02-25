package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.AdminLog;
import com.example.demo.mapper.AdminLogMapper;
import com.example.demo.service.AdminLogService;
import org.springframework.stereotype.Service;

/**
 * 管理员操作日志服务实现类
 */
@Service
public class AdminLogServiceImpl extends ServiceImpl<AdminLogMapper, AdminLog> implements AdminLogService {
}