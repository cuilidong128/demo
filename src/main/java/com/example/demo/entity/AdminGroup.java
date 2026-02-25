package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理员组实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_admin_group")
public class AdminGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组ID
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    private Integer groupId;

    /**
     * 组名称
     */
    private String groupName;
}
