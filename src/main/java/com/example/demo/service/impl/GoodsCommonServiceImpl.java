package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.GoodsCommon;
import com.example.demo.mapper.GoodsCommonMapper;
import com.example.demo.service.GoodsCommonService;
import org.springframework.stereotype.Service;

/**
 * 商品SPU服务实现类
 */
@Service
public class GoodsCommonServiceImpl extends ServiceImpl<GoodsCommonMapper, GoodsCommon> implements GoodsCommonService {
}
