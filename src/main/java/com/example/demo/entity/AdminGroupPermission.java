package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员组权限实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin_group_permission")
public class AdminGroupPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组ID
     */
    private Integer groupId;

    /**
     * 菜单ID
     */
    private Integer menuId;
}
