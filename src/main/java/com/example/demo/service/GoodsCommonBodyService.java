package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.GoodsCommonBody;

/**
 * 商品详情表服务接口
 */
public interface GoodsCommonBodyService extends IService<GoodsCommonBody> {

    /**
     * 根据商品SPU ID获取详情
     */
    GoodsCommonBody getByCommonId(Integer commonId);

    /**
     * 保存或更新商品详情
     */
    boolean saveOrUpdateByCommonId(GoodsCommonBody goodsCommonBody);
}
