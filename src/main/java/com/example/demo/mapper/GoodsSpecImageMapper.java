package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.GoodsSpecImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品SKU管理图片表 Mapper接口
 */
@Mapper
public interface GoodsSpecImageMapper extends BaseMapper<GoodsSpecImage> {

    /**
     * 根据商品SPU ID获取规格图片列表
     */
    List<GoodsSpecImage> selectByCommonId(Integer commonId);
}
