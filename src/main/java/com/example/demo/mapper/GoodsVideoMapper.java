package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.GoodsVideo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品SPU关联音频/视频表 Mapper接口
 */
@Mapper
public interface GoodsVideoMapper extends BaseMapper<GoodsVideo> {

    /**
     * 根据商品SPU ID获取视频列表
     */
    List<GoodsVideo> selectByCommonId(Integer commonId);
}
