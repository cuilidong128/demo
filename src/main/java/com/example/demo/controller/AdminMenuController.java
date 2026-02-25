package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.MenuDTO;
import com.example.demo.entity.Admin;
import com.example.demo.service.AdminMenuService;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员菜单控制器
 */
@Slf4j
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;
    private final AuthService authService;

    /**
     * 获取当前登录用户的菜单列表
     */
    @GetMapping("/current")
    public ApiResponse<List<MenuDTO>> getCurrentUserMenus() {
        log.info("获取当前用户菜单");
        try {
            // 获取当前登录用户
            Admin currentAdmin = authService.getCurrentAdmin();
            if (currentAdmin == null) {
                return ApiResponse.error(401, "未登录");
            }

            // 获取用户菜单
            List<MenuDTO> menus = adminMenuService.getCurrentUserMenus(
                    currentAdmin.getAdminId(),
                    currentAdmin.getIsSuper(),
                    currentAdmin.getGroupId()
            );

            return ApiResponse.success("获取菜单成功", menus);
        } catch (Exception e) {
            log.error("获取当前用户菜单失败: {}", e.getMessage());
            return ApiResponse.error("获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 清除当前用户菜单缓存
     */
    @PostMapping("/clear-cache")
    public ApiResponse<Void> clearMenuCache() {
        log.info("清除当前用户菜单缓存");
        try {
            Admin currentAdmin = authService.getCurrentAdmin();
            if (currentAdmin == null) {
                return ApiResponse.error(401, "未登录");
            }

            adminMenuService.clearMenuCache(currentAdmin.getAdminId());
            return ApiResponse.success("清除缓存成功", null);
        } catch (Exception e) {
            log.error("清除菜单缓存失败: {}", e.getMessage());
            return ApiResponse.error("清除缓存失败: " + e.getMessage());
        }
    }
}

