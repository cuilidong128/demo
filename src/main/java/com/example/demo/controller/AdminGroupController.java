package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.AdminGroup;
import com.example.demo.service.AdminGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员组控制器
 */
@RestController
@RequestMapping("/admin-group")
public class AdminGroupController {

    @Autowired
    private AdminGroupService adminGroupService;

    /**
     * 获取所有管理员组列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        List<AdminGroup> groups = adminGroupService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取管理员组列表成功");
        response.put("data", groups);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取管理员组列表
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getGroupsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<AdminGroup> page = new Page<>(pageNum, pageSize);
        Page<AdminGroup> groupPage = adminGroupService.page(page);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "分页获取管理员组列表成功");
        response.put("data", groupPage);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取管理员组
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Integer id) {
        AdminGroup group = adminGroupService.getById(id);
        Map<String, Object> response = new HashMap<>();
        if (group != null) {
            response.put("code", 200);
            response.put("message", "获取管理员组成功");
            response.put("data", group);
        } else {
            response.put("code", 404);
            response.put("message", "管理员组不存在");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 根据组名获取管理员组
     */
    @GetMapping("/name/{groupName}")
    public ResponseEntity<Map<String, Object>> getGroupByName(@PathVariable String groupName) {
        QueryWrapper<AdminGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_name", groupName);
        AdminGroup group = adminGroupService.getOne(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        if (group != null) {
            response.put("code", 200);
            response.put("message", "获取管理员组成功");
            response.put("data", group);
        } else {
            response.put("code", 404);
            response.put("message", "管理员组不存在");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 创建管理员组
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGroup(@RequestBody AdminGroup group) {
        boolean result = adminGroupService.save(group);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "创建管理员组成功");
            response.put("data", group);
        } else {
            response.put("code", 500);
            response.put("message", "创建管理员组失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 更新管理员组
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateGroup(@RequestBody AdminGroup group) {
        boolean result = adminGroupService.updateById(group);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "更新管理员组成功");
            response.put("data", group);
        } else {
            response.put("code", 500);
            response.put("message", "更新管理员组失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 删除管理员组
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable Integer id) {
        boolean result = adminGroupService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除管理员组成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除管理员组失败");
        }
        return ResponseEntity.ok(response);
    }
}
