package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Attribute;
import com.example.demo.mapper.AttributeMapper;
import com.example.demo.service.AttributeService;
import org.springframework.stereotype.Service;

/**
 * 商品属性服务实现类
 */
@Service
public class AttributeServiceImpl extends ServiceImpl<AttributeMapper, Attribute> implements AttributeService {
}
