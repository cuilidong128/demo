package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Admin;
import com.example.demo.service.AdminService;
import com.example.demo.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有管理员列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllAdmins() {
        List<Admin> admins = adminService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取管理员列表成功");
        response.put("data", admins);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取管理员列表
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getAdminsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Admin> page = new Page<>(pageNum, pageSize);
        Page<Admin> adminPage = adminService.page(page);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "分页获取管理员列表成功");
        response.put("data", adminPage);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取管理员
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAdminById(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (admin != null) {
            response.put("code", 200);
            response.put("message", "获取管理员成功");
            response.put("data", admin);
        } else {
            response.put("code", 404);
            response.put("message", "管理员不存在");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户名获取管理员
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> getAdminByName(@PathVariable String name) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Admin admin = adminService.getOne(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        if (admin != null) {
            response.put("code", 200);
            response.put("message", "获取管理员成功");
            response.put("data", admin);
        } else {
            response.put("code", 404);
            response.put("message", "管理员不存在");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 创建管理员
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createAdmin(@RequestBody Admin admin) {
        // 对密码进行MD5加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String md5Password = Md5Util.encrypt(admin.getPassword());
            admin.setPassword(md5Password);
        }

        boolean result = adminService.save(admin);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "创建管理员成功");
            response.put("data", admin);
        } else {
            response.put("code", 500);
            response.put("message", "创建管理员失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 更新管理员
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAdmin(@RequestBody Admin admin) {
        // 如果密码字段不为空，则进行MD5加密
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            String md5Password = Md5Util.encrypt(admin.getPassword());
            admin.setPassword(md5Password);
        }

        boolean result = adminService.updateById(admin);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "更新管理员成功");
            response.put("data", admin);
        } else {
            response.put("code", 500);
            response.put("message", "更新管理员失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdmin(@PathVariable Integer id) {
        boolean result = adminService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除管理员成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除管理员失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 解锁管理员账户
     */
    @PostMapping("/unlock/{id}")
    public ResponseEntity<Map<String, Object>> unlockAdmin(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        Map<String, Object> response = new HashMap<>();

        if (admin == null) {
            response.put("code", 404);
            response.put("message", "管理员不存在");
            return ResponseEntity.ok(response);
        }

        // 确保groupId不为null且大于0
        if (admin.getGroupId() == null || admin.getGroupId() <= 0) {
            admin.setGroupId(1); // 设置默认组ID
            boolean result = adminService.updateById(admin);
            if (result) {
                response.put("code", 200);
                response.put("message", "管理员账户解锁成功");
                response.put("data", admin);
            } else {
                response.put("code", 500);
                response.put("message", "解锁管理员账户失败");
            }
        } else {
            response.put("code", 200);
            response.put("message", "管理员账户已经是激活状态");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteAdmins(@RequestBody List<Integer> ids) {
        boolean result = adminService.removeByIds(ids);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "批量删除管理员成功");
        } else {
            response.put("code", 500);
            response.put("message", "批量删除管理员失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户组ID获取管理员列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> getAdminsByGroupId(@PathVariable Integer groupId) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("groupId", groupId);
        List<Admin> admins = adminService.list(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取用户组管理员列表成功");
        response.put("data", admins);
        return ResponseEntity.ok(response);
    }
}
