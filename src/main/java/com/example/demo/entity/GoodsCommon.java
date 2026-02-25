package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品SPU实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_goods_common", autoResultMap = true)
public class GoodsCommon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SPU ID
     */
    @TableId(value = "common_id", type = IdType.AUTO)
    private Integer commonId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品卖点
     */
    private String jingle;

    /**
     * 类目ID列表，多个用逗号分隔
     */
    private String categoryId;

    /**
     * 一级类目ID
     */
    @TableField(value = "category_id_1")
    private Integer categoryId1;

    /**
     * 二级类目ID
     */
    @TableField(value = "category_id_2")
    private Integer categoryId2;

    /**
     * 三级类目ID
     */
    @TableField(value = "category_id_3")
    private Integer categoryId3;

    /**
     * 品牌ID
     */
    private Integer brandId;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 商品状态:0下架,1正常,10违规禁售
     */
    private Integer goodsState;

    /**
     * 审核状态:0未通过,1已通过,10审核中
     */
    private Integer goodsVerify;

    /**
     * 主图
     */
    private String goodsImage;

    /**
     * 轮播图列表，JSON格式存储
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> goodsImages;

    /**
     * 商品价格
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
     * 商品库存
     */
    private Integer goodsStorage;

    /**
     * 销售数量
     */
    private Integer goodsSaleNum;

    /**
     * 点击数量
     */
    private Integer goodsClick;

    /**
     * 收藏数量
     */
    private Integer goodsFavorite;

    /**
     * 评价数量
     */
    private Integer evaluateNum;

    /**
     * 好评率
     */
    private Integer goodsRate;

    /**
     * 运费模板ID
     */
    private Integer freightTemplateId;

    /**
     * 商品重量(kg)
     */
    private BigDecimal freightWeight;

    /**
     * 商品体积(m³)
     */
    private BigDecimal freightVolume;

    /**
     * 计量单位
     */
    private String unitName;

    /**
     * 是否推荐:0否,1是
     */
    private Integer isCommend;

    /**
     * 是否分销:0否,1是
     */
    private Integer isDistribution;

    /**
     * 是否积分商品:0否,1是
     */
    private Integer isPointsGoods;

    /**
     * 是否有赠品:0否,1是
     */
    private Integer isGift;

    /**
     * 分销佣金比例%
     */
    private Integer commissionRate;

    /**
     * 搜索优先级
     */
    private Integer searchBoost;

    /**
     * 规格JSON
     */
    private String specJson;

    /**
     * 规格名称JSON
     */
    private String goodsSpecNames;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
