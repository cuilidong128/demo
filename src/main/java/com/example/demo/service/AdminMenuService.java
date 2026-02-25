package com.example.demo.service;

import com.example.demo.dto.MenuDTO;

import java.util.List;

/**
 * 管理员菜单服务接口
 */
public interface AdminMenuService {

    /**
     * 获取当前登录用户的菜单列表
     *
     * @param adminId 管理员ID
     * @param isSuper 是否超级管理员
     * @param groupId 组ID
     * @return 菜单列表
     */
    List<MenuDTO> getCurrentUserMenus(Integer adminId, Integer isSuper, Integer groupId);

    /**
     * 从缓存获取用户菜单
     *
     * @param adminId 管理员ID
     * @return 菜单列表
     */
    List<MenuDTO> getMenusFromCache(Integer adminId);

    /**
     * 清除用户菜单缓存
     *
     * @param adminId 管理员ID
     */
    void clearMenuCache(Integer adminId);
}