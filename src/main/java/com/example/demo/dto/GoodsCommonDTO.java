package com.example.demo.dto;

import com.example.demo.entity.Goods;
import com.example.demo.entity.GoodsCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品SPU DTO（包含SKU列表和商品详情）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsCommonDTO extends GoodsCommon {

    private static final long serialVersionUID = 1L;

    /**
     * SKU列表
     */
    private List<Goods> skuList;

    /**
     * PC端商品详情
     */
    private String goodsBody;

    /**
     * 移动端商品详情
     */
    private String mobileBody;
}
