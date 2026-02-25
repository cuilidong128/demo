package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商品SKU管理图片表实体类
 * 存储SKU规格对应的图片
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_goods_spec_image")
public class GoodsSpecImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格图片ID
     */
    @TableId(value = "spec_image_id", type = IdType.AUTO)
    private Integer specImageId;

    /**
     * 商品SPU ID
     */
    private Integer commonId;

    /**
     * 图片名称/路径
     */
    private String imageName;
}
