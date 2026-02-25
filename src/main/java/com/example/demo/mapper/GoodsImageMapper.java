package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.GoodsImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品图片表 Mapper接口
 */
@Mapper
public interface GoodsImageMapper extends BaseMapper<GoodsImage> {

    /**
     * 根据商品SPU ID获取图片列表
     */
    List<GoodsImage> selectByCommonId(Integer commonId);
}
