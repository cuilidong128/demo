package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.GoodsVideo;
import com.example.demo.mapper.GoodsVideoMapper;
import com.example.demo.service.GoodsVideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品SPU关联音频/视频表服务实现类
 */
@Service
public class GoodsVideoServiceImpl extends ServiceImpl<GoodsVideoMapper, GoodsVideo> implements GoodsVideoService {

    @Override
    public List<GoodsVideo> getByCommonId(Integer commonId) {
        QueryWrapper<GoodsVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        wrapper.orderByAsc("video_sort");
        return list(wrapper);
    }

    @Override
    @Transactional
    public boolean setDefaultVideo(Integer videoId, Integer commonId) {
        // 先将该商品的所有视频设为非默认
        QueryWrapper<GoodsVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        GoodsVideo updateAll = new GoodsVideo();
        updateAll.setIsDefault(0);
        update(updateAll, wrapper);

        // 再将指定视频设为默认
        GoodsVideo defaultVideo = new GoodsVideo();
        defaultVideo.setVideoId(videoId);
        defaultVideo.setIsDefault(1);
        return updateById(defaultVideo);
    }
}
