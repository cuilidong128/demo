package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.GoodsSpecImage;
import com.example.demo.mapper.GoodsSpecImageMapper;
import com.example.demo.service.GoodsSpecImageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品SKU管理图片表服务实现类
 */
@Service
public class GoodsSpecImageServiceImpl extends ServiceImpl<GoodsSpecImageMapper, GoodsSpecImage> implements GoodsSpecImageService {

    @Override
    public List<GoodsSpecImage> getByCommonId(Integer commonId) {
        QueryWrapper<GoodsSpecImage> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        return list(wrapper);
    }
}
