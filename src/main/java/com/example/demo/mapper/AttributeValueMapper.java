package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.AttributeValue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性值Mapper接口
 */
@Mapper
public interface AttributeValueMapper extends BaseMapper<AttributeValue> {
}
