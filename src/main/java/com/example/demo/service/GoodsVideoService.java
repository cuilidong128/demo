package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.GoodsVideo;

import java.util.List;

/**
 * 商品SPU关联音频/视频表服务接口
 */
public interface GoodsVideoService extends IService<GoodsVideo> {

    /**
     * 根据商品SPU ID获取视频列表
     */
    List<GoodsVideo> getByCommonId(Integer commonId);

    /**
     * 设置默认视频
     */
    boolean setDefaultVideo(Integer videoId, Integer commonId);
}
