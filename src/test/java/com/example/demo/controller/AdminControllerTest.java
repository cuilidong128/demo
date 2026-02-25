package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Admin;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAdmins_Success() {
        // 准备测试数据
        List<Admin> admins = new ArrayList<>();
        Admin admin1 = new Admin();
        admin1.setAdminId(1);
        admin1.setName("admin1");
        admin1.setIsSuper(1);
        admins.add(admin1);

        Admin admin2 = new Admin();
        admin2.setAdminId(2);
        admin2.setName("admin2");
        admin2.setIsSuper(0);
        admins.add(admin2);

        // 设置mock行为
        when(adminService.list()).thenReturn(admins);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.getAllAdmins();

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("获取管理员列表成功", body.get("message"));
        assertEquals(admins, body.get("data"));

        // 验证方法调用
        verify(adminService, times(1)).list();
    }

    @Test
    void testGetAdminsByPage_Success() {
        // 准备测试数据
        Page<Admin> page = new Page<>(1, 10);
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setName("test_admin");
        page.setRecords(Arrays.asList(admin));
        page.setTotal(1L);

        // 设置mock行为
        when(adminService.page(any(Page.class))).thenReturn(page);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.getAdminsByPage(1, 10);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("分页获取管理员列表成功", body.get("message"));
        assertTrue(body.get("data") instanceof Page);

        // 验证方法调用
        verify(adminService, times(1)).page(any(Page.class));
    }

    @Test
    void testGetAdminById_Found() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setName("test_admin");

        // 设置mock行为
        when(adminService.getById(1)).thenReturn(admin);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.getAdminById(1);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("获取管理员成功", body.get("message"));
        assertEquals(admin, body.get("data"));

        // 验证方法调用
        verify(adminService, times(1)).getById(1);
    }

    @Test
    void testGetAdminById_NotFound() {
        // 设置mock行为
        when(adminService.getById(999)).thenReturn(null);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.getAdminById(999);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.get("code"));
        assertEquals("管理员不存在", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).getById(999);
    }

    @Test
    void testCreateAdmin_Success() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setName("new_admin");
        admin.setPassword("password123");
        admin.setIsSuper(0);

        // 设置mock行为
        when(adminService.save(any(Admin.class))).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.createAdmin(admin);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("创建管理员成功", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).save(any(Admin.class));
    }

    @Test
    void testCreateAdmin_Failure() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setName("new_admin");

        // 设置mock行为
        when(adminService.save(any(Admin.class))).thenReturn(false);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.createAdmin(admin);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("code"));
        assertEquals("创建管理员失败", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).save(any(Admin.class));
    }

    @Test
    void testUpdateAdmin_Success() {
        // 准备测试数据
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setName("updated_admin");

        // 设置mock行为
        when(adminService.updateById(any(Admin.class))).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.updateAdmin(admin);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("更新管理员成功", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).updateById(any(Admin.class));
    }

    @Test
    void testDeleteAdmin_Success() {
        // 设置mock行为
        when(adminService.removeById(1)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.deleteAdmin(1);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("删除管理员成功", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).removeById(1);
    }

    @Test
    void testBatchDeleteAdmins_Success() {
        // 准备测试数据
        List<Integer> ids = Arrays.asList(1, 2, 3);

        // 设置mock行为
        when(adminService.removeByIds(ids)).thenReturn(true);

        // 执行测试
        ResponseEntity<Map<String, Object>> response = adminController.batchDeleteAdmins(ids);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("批量删除管理员成功", body.get("message"));

        // 验证方法调用
        verify(adminService, times(1)).removeByIds(ids);
    }
}