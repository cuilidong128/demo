package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品属性值实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_attribute_value")
public class AttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性值ID
     */
    @TableId(value = "attribute_value_id", type = IdType.AUTO)
    private Integer attributeValueId;

    /**
     * 属性ID
     */
    private Integer attributeId;

    /**
     * 属性值名称
     */
    private String attributeValueName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
