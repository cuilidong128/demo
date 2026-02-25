package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.AdminMenu;
import com.example.demo.mapper.AdminMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器（需要超级管理员权限）
 */
@Slf4j
@RestController
@RequestMapping("/admin-menu")
@RequiredArgsConstructor
public class AdminMenuManageController {

    private final AdminMenuMapper adminMenuMapper;

    /**
     * 获取所有菜单列表（扁平结构）
     */
    @GetMapping("/list")
    public ApiResponse<List<AdminMenu>> getMenuList() {
        log.info("获取所有菜单列表");
        try {
            List<AdminMenu> menus = adminMenuMapper.selectList(null);
            return ApiResponse.success("获取菜单列表成功", menus);
        } catch (Exception e) {
            log.error("获取菜单列表失败: {}", e.getMessage());
            return ApiResponse.error("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有菜单树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getMenuTree() {
        log.info("获取菜单树形结构");
        try {
            List<AdminMenu> menus = adminMenuMapper.selectList(null);
            List<Map<String, Object>> tree = buildMenuTree(menus);
            return ApiResponse.success("获取菜单树成功", tree);
        } catch (Exception e) {
            log.error("获取菜单树失败: {}", e.getMessage());
            return ApiResponse.error("获取菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取菜单
     */
    @GetMapping("/{id}")
    public ApiResponse<AdminMenu> getMenuById(@PathVariable Integer id) {
        log.info("根据ID获取菜单, id: {}", id);
        try {
            AdminMenu menu = adminMenuMapper.selectById(id);
            if (menu != null) {
                return ApiResponse.success("获取菜单成功", menu);
            } else {
                return ApiResponse.error(404, "菜单不存在");
            }
        } catch (Exception e) {
            log.error("获取菜单失败: {}", e.getMessage());
            return ApiResponse.error("获取菜单失败: " + e.getMessage());
        }
    }

    /**
     * 创建菜单
     */
    @PostMapping("/create")
    public ApiResponse<AdminMenu> createMenu(@RequestBody AdminMenu menu) {
        log.info("创建菜单: {}, id: {}", menu.getName(), menu.getId());
        try {
            // 如果parentId为null，设置为0
            if (menu.getParentId() == null) {
                menu.setParentId(0);
            }
            // 如果id为空，需要生成一个（因为数据库可能要求有id）
            if (menu.getId() == null) {
                // 查询当前最大ID，然后+1
                Integer maxId = adminMenuMapper.selectList(null).stream()
                        .mapToInt(AdminMenu::getId)
                        .max()
                        .orElse(0);
                menu.setId(maxId + 1);
            }
            adminMenuMapper.insert(menu);
            return ApiResponse.success("创建菜单成功", menu);
        } catch (Exception e) {
            log.error("创建菜单失败: {}", e.getMessage());
            return ApiResponse.error("创建菜单失败: " + e.getMessage());
        }
    }

    /**
     * 更新菜单
     */
    @PutMapping("/update")
    public ApiResponse<AdminMenu> updateMenu(@RequestBody AdminMenu menu) {
        log.info("更新菜单: {}", menu.getId());
        try {
            // 如果parentId为null，设置为0
            if (menu.getParentId() == null) {
                menu.setParentId(0);
            }
            adminMenuMapper.updateById(menu);
            return ApiResponse.success("更新菜单成功", menu);
        } catch (Exception e) {
            log.error("更新菜单失败: {}", e.getMessage());
            return ApiResponse.error("更新菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单（同时删除子菜单）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMenu(@PathVariable Integer id) {
        log.info("删除菜单: {}", id);
        try {
            // 先删除子菜单
            deleteChildrenMenus(id);
            // 删除当前菜单
            adminMenuMapper.deleteById(id);
            return ApiResponse.success("删除菜单成功", null);
        } catch (Exception e) {
            log.error("删除菜单失败: {}", e.getMessage());
            return ApiResponse.error("删除菜单失败: " + e.getMessage());
        }
    }

    /**
     * 递归删除子菜单
     */
    private void deleteChildrenMenus(Integer parentId) {
        QueryWrapper<AdminMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<AdminMenu> children = adminMenuMapper.selectList(wrapper);
        for (AdminMenu child : children) {
            deleteChildrenMenus(child.getId());
            adminMenuMapper.deleteById(child.getId());
        }
    }

    /**
     * 构建菜单树
     */
    private List<Map<String, Object>> buildMenuTree(List<AdminMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为Map列表
        List<Map<String, Object>> menuList = menus.stream().map(menu -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", menu.getId());
            map.put("name", menu.getName());
            map.put("title", menu.getTitle());
            map.put("url", menu.getUrl());
            map.put("parentId", menu.getParentId());
            map.put("permission", menu.getPermission());
            map.put("groupId", menu.getGroupId());
            map.put("children", new ArrayList<Map<String, Object>>());
            return map;
        }).collect(Collectors.toList());

        // 使用Map存储所有菜单
        Map<Integer, Map<String, Object>> menuMap = menuList.stream()
                .collect(Collectors.toMap(m -> (Integer) m.get("id"), m -> m));

        // 构建树形结构
        List<Map<String, Object>> rootMenus = new ArrayList<>();
        for (Map<String, Object> menu : menuList) {
            Integer parentId = (Integer) menu.get("parentId");
            if (parentId == null || parentId == 0 || !menuMap.containsKey(parentId)) {
                rootMenus.add(menu);
            } else {
                Map<String, Object> parent = menuMap.get(parentId);
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(menu);
                }
            }
        }

        return rootMenus;
    }
}
