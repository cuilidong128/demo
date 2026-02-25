package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商品图片表实体类
 * 支持本地文件上传和阿里云OSS上传
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_goods_image")
public class GoodsImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Integer imageId;

    /**
     * 颜色规格ID
     */
    private Integer colorId;

    /**
     * 商品SPU ID
     */
    private Integer commonId;

    /**
     * 图片名称/路径
     * 本地路径或阿里云OSS地址
     */
    private String imageName;

    /**
     * 图片排序
     */
    private Integer imageSort;

    /**
     * 是否默认图：0否，1是
     */
    private Integer isDefault;
}
