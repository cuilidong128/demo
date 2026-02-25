package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.AdminGroup;
import com.example.demo.mapper.AdminGroupMapper;
import com.example.demo.service.AdminGroupService;
import org.springframework.stereotype.Service;

/**
 * 管理员组服务实现类
 */
@Service
public class AdminGroupServiceImpl extends ServiceImpl<AdminGroupMapper, AdminGroup> implements AdminGroupService {
}