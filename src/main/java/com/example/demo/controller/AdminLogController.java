package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.AdminLog;
import com.example.demo.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员操作日志控制器
 */
@RestController
@RequestMapping("/admin-log")
public class AdminLogController {

    @Autowired
    private AdminLogService adminLogService;

    /**
     * 获取所有操作日志列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllLogs() {
        List<AdminLog> logs = adminLogService.list();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取操作日志列表成功");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取操作日志列表
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getLogsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<AdminLog> page = new Page<>(pageNum, pageSize);
        Page<AdminLog> logPage = adminLogService.page(page);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "分页获取操作日志列表成功");
        response.put("data", logPage);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据管理员ID获取操作日志
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<Map<String, Object>> getLogsByAdminId(@PathVariable Integer adminId) {
        QueryWrapper<AdminLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("admin_id", adminId);
        queryWrapper.orderByDesc("create_time");
        List<AdminLog> logs = adminLogService.list(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取管理员操作日志成功");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据时间范围获取操作日志
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        QueryWrapper<AdminLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("create_time", startTime, endTime);
        queryWrapper.orderByDesc("create_time");
        List<AdminLog> logs = adminLogService.list(queryWrapper);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取时间范围内操作日志成功");
        response.put("data", logs);
        return ResponseEntity.ok(response);
    }

    /**
     * 创建操作日志
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLog(@RequestBody AdminLog log) {
        // 如果没有设置创建时间，则使用当前时间
        if (log.getCreateTime() == null) {
            log.setCreateTime(LocalDateTime.now());
        }

        boolean result = adminLogService.save(log);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "创建操作日志成功");
            response.put("data", log);
        } else {
            response.put("code", 500);
            response.put("message", "创建操作日志失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 批量创建操作日志
     */
    @PostMapping("/batch-create")
    public ResponseEntity<Map<String, Object>> batchCreateLogs(@RequestBody List<AdminLog> logs) {
        // 为没有设置创建时间的日志设置当前时间
        logs.forEach(log -> {
            if (log.getCreateTime() == null) {
                log.setCreateTime(LocalDateTime.now());
            }
        });

        boolean result = adminLogService.saveBatch(logs);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "批量创建操作日志成功");
            response.put("data", logs);
        } else {
            response.put("code", 500);
            response.put("message", "批量创建操作日志失败");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLog(@PathVariable Integer id) {
        boolean result = adminLogService.removeById(id);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除操作日志成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除操作日志失败");
        }
        return ResponseEntity.ok(response);
    }
}
