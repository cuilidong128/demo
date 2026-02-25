package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.GoodsImage;

import java.util.List;

/**
 * 商品图片表服务接口
 */
public interface GoodsImageService extends IService<GoodsImage> {

    /**
     * 根据商品SPU ID获取图片列表
     */
    List<GoodsImage> getByCommonId(Integer commonId);

    /**
     * 设置默认图片
     */
    boolean setDefaultImage(Integer imageId, Integer commonId);
}
