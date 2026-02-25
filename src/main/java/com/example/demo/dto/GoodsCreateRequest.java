package com.example.demo.dto;

import com.example.demo.entity.Goods;
import com.example.demo.entity.GoodsCommon;
import lombok.Data;

import java.util.List;

/**
 * 商品创建请求DTO
 * 包含SPU、SKU列表和商品详情
 */
@Data
public class GoodsCreateRequest {

    /**
     * 商品SPU信息
     */
    private GoodsCommon spu;

    /**
     * SKU列表
     */
    private List<Goods> skuList;

    /**
     * 轮播图列表
     */
    private List<String> goodsImages;

    /**
     * 商品类目ID
     */
    private String categoryId;

    /**
     * PC端商品详情（富文本HTML）
     */
    private String goodsBody;

    /**
     * 移动端商品详情（富文本HTML）
     */
    private String mobileBody;
}
