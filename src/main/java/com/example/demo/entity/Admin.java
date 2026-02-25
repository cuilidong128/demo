package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Integer adminId;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 组ID
     */
    @TableField(value = "groupId")
    private Integer groupId;

    /**
     * 组名称
     */
    @TableField(value = "groupName")
    private String groupName;

    /**
     * 是否超级管理员 1是 0否
     */
    @TableField(value = "isSuper")
    private Integer isSuper;

    /**
     * 管理员姓名
     */
    private String name;

    /**
     * 密码
     */
    private String password;
}
