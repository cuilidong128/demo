package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员菜单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin_menu")
public class AdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private Integer id;

    /**
     * 组ID
     */
    private Integer groupId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父级ID
     */
    private Integer parentId;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单URL
     */
    private String url;
}
