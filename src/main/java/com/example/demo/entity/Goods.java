package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_goods")
public class Goods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU ID
     */
    @TableId(value = "goods_id", type = IdType.AUTO)
    private Integer goodsId;

    /**
     * SPU ID
     */
    private Integer commonId;

    /**
     * SKU名称
     */
    private String goodsName;

    /**
     * 规格值组合
     */
    private String goodsSpecs;

    /**
     * 完整规格信息
     */
    private String goodsFullSpecs;

    /**
     * 销售价格
     */
    private BigDecimal goodsPrice;

    /**
     * 市场价
     */
    private BigDecimal goodsMarketPrice;

    /**
     * 成本价
     */
    private BigDecimal goodsCostPrice;

    /**
     * 库存
     */
    private Integer goodsStorage;

    /**
     * 库存预警值
     */
    private Integer goodsStorageAlarm;

    /**
     * 商品货号
     */
    private String goodsSerial;

    /**
     * 商品条形码
     */
    private String goodsBarcode;

    /**
     * SKU图片
     */
    private String imageName;

    /**
     * 颜色ID
     */
    private Integer colorId;

    /**
     * 是否默认:0否,1是
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
