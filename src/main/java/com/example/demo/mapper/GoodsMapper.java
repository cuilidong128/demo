package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品SKU Mapper接口
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
