package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.GoodsSpecImage;

import java.util.List;

/**
 * 商品SKU管理图片表服务接口
 */
public interface GoodsSpecImageService extends IService<GoodsSpecImage> {

    /**
     * 根据商品SPU ID获取规格图片列表
     */
    List<GoodsSpecImage> getByCommonId(Integer commonId);
}
