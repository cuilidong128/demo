package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.dto.MenuDTO;
import com.example.demo.entity.AdminGroupPermission;
import com.example.demo.entity.AdminMenu;
import com.example.demo.mapper.AdminGroupPermissionMapper;
import com.example.demo.mapper.AdminMenuMapper;
import com.example.demo.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员菜单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMenuServiceImpl implements AdminMenuService {

    private final AdminMenuMapper adminMenuMapper;
    private final AdminGroupPermissionMapper adminGroupPermissionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String MENU_CACHE_KEY_PREFIX = "admin:menu:";
    private static final long MENU_CACHE_EXPIRE = 24; // 缓存24小时

    @Override
    public List<MenuDTO> getCurrentUserMenus(Integer adminId, Integer isSuper, Integer groupId) {
        // 先尝试从缓存获取
        List<MenuDTO> cachedMenus = getMenusFromCache(adminId);
        if (cachedMenus != null && !cachedMenus.isEmpty()) {
            log.debug("从缓存获取用户菜单, adminId: {}", adminId);
            return cachedMenus;
        }

        // 查询菜单
        List<AdminMenu> menus;
        if (isSuper != null && isSuper == 1) {
            // 超级管理员查询所有菜单
            log.info("超级管理员查询所有菜单, adminId: {}", adminId);
            menus = adminMenuMapper.selectList(null);
        } else {
            // 普通管理员根据groupId查询权限
            log.info("普通管理员查询菜单, adminId: {}, groupId: {}", adminId, groupId);
            menus = getMenusByGroupId(groupId);
        }

        // 转换为DTO并构建树形结构
        List<MenuDTO> menuDTOs = buildMenuTree(menus);

        // 存入缓存
        saveMenusToCache(adminId, menuDTOs);

        return menuDTOs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MenuDTO> getMenusFromCache(Integer adminId) {
        String cacheKey = MENU_CACHE_KEY_PREFIX + adminId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return (List<MenuDTO>) cached;
            }
        } catch (Exception e) {
            log.warn("从Redis获取菜单缓存失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void clearMenuCache(Integer adminId) {
        String cacheKey = MENU_CACHE_KEY_PREFIX + adminId;
        try {
            redisTemplate.delete(cacheKey);
            log.info("清除用户菜单缓存, adminId: {}", adminId);
        } catch (Exception e) {
            log.warn("清除Redis菜单缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 根据组ID获取菜单
     */
    private List<AdminMenu> getMenusByGroupId(Integer groupId) {
        if (groupId == null) {
            return new ArrayList<>();
        }

        // 查询组权限
        QueryWrapper<AdminGroupPermission> permissionWrapper = new QueryWrapper<>();
        permissionWrapper.eq("group_id", groupId);
        List<AdminGroupPermission> permissions = adminGroupPermissionMapper.selectList(permissionWrapper);

        if (permissions.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取菜单ID列表
        List<Integer> menuIds = permissions.stream()
                .map(AdminGroupPermission::getMenuId)
                .collect(Collectors.toList());

        // 查询菜单
        QueryWrapper<AdminMenu> menuWrapper = new QueryWrapper<>();
        menuWrapper.in("id", menuIds);
        return adminMenuMapper.selectList(menuWrapper);
    }

    /**
     * 构建菜单树
     */
    private List<MenuDTO> buildMenuTree(List<AdminMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为DTO
        List<MenuDTO> dtoList = menus.stream().map(menu ->
                MenuDTO.builder()
                        .id(menu.getId())
                        .name(menu.getName())
                        .title(menu.getTitle())
                        .path(menu.getUrl())
                        .parentId(menu.getParentId())
                        .permission(menu.getPermission())
                        .children(new ArrayList<>())
                        .build()
        ).collect(Collectors.toList());

        // 使用Map存储所有菜单，方便查找
        Map<Integer, MenuDTO> menuMap = dtoList.stream()
                .collect(Collectors.toMap(MenuDTO::getId, menu -> menu));

        // 构建树形结构
        List<MenuDTO> rootMenus = new ArrayList<>();
        for (MenuDTO menu : dtoList) {
            Integer parentId = menu.getParentId();
            // 判断是否为根节点：parentId为null、0，或者在map中找不到父节点
            if (parentId == null || parentId == 0 || !menuMap.containsKey(parentId)) {
                rootMenus.add(menu);
            } else {
                // 找到父节点并添加为子节点
                MenuDTO parent = menuMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        log.info("构建菜单树完成，总菜单数: {}，根节点数: {}", dtoList.size(), rootMenus.size());
        return rootMenus;
    }

    /**
     * 保存菜单到缓存
     */
    private void saveMenusToCache(Integer adminId, List<MenuDTO> menus) {
        String cacheKey = MENU_CACHE_KEY_PREFIX + adminId;
        try {
            redisTemplate.opsForValue().set(cacheKey, menus, MENU_CACHE_EXPIRE, TimeUnit.HOURS);
            log.debug("用户菜单已缓存, adminId: {}", adminId);
        } catch (Exception e) {
            log.warn("保存菜单到Redis缓存失败: {}", e.getMessage());
        }
    }
}
