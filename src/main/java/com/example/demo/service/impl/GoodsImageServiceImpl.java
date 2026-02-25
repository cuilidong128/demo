package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.GoodsImage;
import com.example.demo.mapper.GoodsImageMapper;
import com.example.demo.service.GoodsImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品图片表服务实现类
 */
@Service
public class GoodsImageServiceImpl extends ServiceImpl<GoodsImageMapper, GoodsImage> implements GoodsImageService {

    @Override
    public List<GoodsImage> getByCommonId(Integer commonId) {
        QueryWrapper<GoodsImage> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        wrapper.orderByAsc("image_sort");
        return list(wrapper);
    }

    @Override
    @Transactional
    public boolean setDefaultImage(Integer imageId, Integer commonId) {
        // 先将该商品的所有图片设为非默认
        QueryWrapper<GoodsImage> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        GoodsImage updateAll = new GoodsImage();
        updateAll.setIsDefault(0);
        update(updateAll, wrapper);

        // 再将指定图片设为默认
        GoodsImage defaultImage = new GoodsImage();
        defaultImage.setImageId(imageId);
        defaultImage.setIsDefault(1);
        return updateById(defaultImage);
    }
}
