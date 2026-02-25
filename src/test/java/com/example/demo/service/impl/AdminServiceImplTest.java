package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Admin;
import com.example.demo.mapper.AdminMapper;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAdmin() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setName("test_admin");
        admin.setPassword("password123");

        // 设置mock行为
        when(adminMapper.insert(admin)).thenReturn(1);

        // 执行测试
        boolean result = adminService.save(admin);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(adminMapper, times(1)).insert(admin);
    }

    @Test
    void testRemoveById() {
        // 设置mock行为
        when(adminMapper.deleteById(1)).thenReturn(1);

        // 执行测试
        boolean result = adminService.removeById(1);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(adminMapper, times(1)).deleteById(1);
    }

    @Test
    void testUpdateById() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setName("updated_admin");

        // 设置mock行为
        when(adminMapper.updateById(admin)).thenReturn(1);

        // 执行测试
        boolean result = adminService.updateById(admin);

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(adminMapper, times(1)).updateById(admin);
    }
}