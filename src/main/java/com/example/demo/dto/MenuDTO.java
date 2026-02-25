package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单DTO
 */
@Data
@Builder
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单URL/路由路径
     */
    private String path;

    /**
     * 父级ID
     */
    private Integer parentId;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 子菜单列表
     */
    private List<MenuDTO> children;
}