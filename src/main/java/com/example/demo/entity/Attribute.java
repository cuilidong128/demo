package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品属性实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_attribute")
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性ID
     */
    @TableId(value = "attribute_id", type = IdType.AUTO)
    private Integer attributeId;

    /**
     * 属性名称
     */
    private String attributeName;

    /**
     * 排序
     */
    private Integer attributeSort;

    /**
     * 所属类目ID
     */
    private Integer categoryId;

    /**
     * 是否显示:0否,1是
     */
    private Integer isShow;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
