package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.AdminGroupPermission;
import com.example.demo.service.AdminGroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员组权限控制器
 */
@RestController
@RequestMapping("/admin-group-permission")
public class AdminGroupPermissionController {

    @Autowired
    private AdminGroupPermissionService adminGroupPermissionService;

    /**
     * 获取所有组权限列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllPermissions() {
        List<AdminGroupPermission> permissions = adminGroupPermissionService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取组权限列表成功");
        response.put("data", permissions);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据组ID获取权限列表
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> getPermissionsByGroupId(@PathVariable Integer groupId) {
        QueryWrapper<AdminGroupPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        List<AdminGroupPermission> permissions = adminGroupPermissionService.list(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取组权限成功");
        response.put("data", permissions);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建组权限
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody AdminGroupPermission permission) {
        boolean result = adminGroupPermissionService.save(permission);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "创建组权限成功");
            response.put("data", permission);
        } else {
            response.put("code", 500);
            response.put("message", "创建组权限失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 批量创建组权限
     */
    @PostMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> batchCreatePermissions(@RequestBody List<AdminGroupPermission> permissions) {
        boolean result = adminGroupPermissionService.saveBatch(permissions);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "批量创建组权限成功");
            response.put("data", permissions);
        } else {
            response.put("code", 500);
            response.put("message", "批量创建组权限失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 删除组权限
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePermission(@PathVariable Integer id) {
        boolean result = adminGroupPermissionService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除组权限成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除组权限失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 根据组ID删除所有权限
     */
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> deletePermissionsByGroupId(@PathVariable Integer groupId) {
        QueryWrapper<AdminGroupPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        boolean result = adminGroupPermissionService.remove(queryWrapper);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除组权限成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除组权限失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 设置用户组菜单权限（先删除再批量添加）
     */
    @PostMapping("/set-permissions")
    public ResponseEntity<Map<String, Object>> setGroupPermissions(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer groupId = (Integer) params.get("groupId");
            @SuppressWarnings("unchecked")
            List<Integer> menuIds = (List<Integer>) params.get("menuIds");

            if (groupId == null) {
                response.put("code", 400);
                response.put("message", "用户组ID不能为空");
                return ResponseEntity.ok(response);
            }

            // 先删除该组的所有权限
            QueryWrapper<AdminGroupPermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_id", groupId);
            adminGroupPermissionService.remove(queryWrapper);

            // 批量添加新的权限
            if (menuIds != null && !menuIds.isEmpty()) {
                List<AdminGroupPermission> permissions = menuIds.stream()
                        .map(menuId -> {
                            AdminGroupPermission permission = new AdminGroupPermission();
                            permission.setGroupId(groupId);
                            permission.setMenuId(menuId);
                            return permission;
                        })
                        .collect(Collectors.toList());
                adminGroupPermissionService.saveBatch(permissions);
            }

            response.put("code", 200);
            response.put("message", "设置权限成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "设置权限失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
