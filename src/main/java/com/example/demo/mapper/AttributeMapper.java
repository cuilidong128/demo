package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Attribute;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性Mapper接口
 */
@Mapper
public interface AttributeMapper extends BaseMapper<Attribute> {
}
