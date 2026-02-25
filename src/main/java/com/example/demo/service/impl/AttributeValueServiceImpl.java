package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.AttributeValue;
import com.example.demo.mapper.AttributeValueMapper;
import com.example.demo.service.AttributeValueService;
import org.springframework.stereotype.Service;

/**
 * 商品属性值服务实现类
 */
@Service
public class AttributeValueServiceImpl extends ServiceImpl<AttributeValueMapper, AttributeValue> implements AttributeValueService {
}
