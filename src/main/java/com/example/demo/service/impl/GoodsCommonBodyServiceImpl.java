package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.GoodsCommonBody;
import com.example.demo.mapper.GoodsCommonBodyMapper;
import com.example.demo.service.GoodsCommonBodyService;
import org.springframework.stereotype.Service;

/**
 * 商品详情表服务实现类
 */
@Service
public class GoodsCommonBodyServiceImpl extends ServiceImpl<GoodsCommonBodyMapper, GoodsCommonBody> implements GoodsCommonBodyService {

    @Override
    public GoodsCommonBody getByCommonId(Integer commonId) {
        return getById(commonId);
    }

    @Override
    public boolean saveOrUpdateByCommonId(GoodsCommonBody goodsCommonBody) {
        if (goodsCommonBody.getCommonId() == null) {
            return false;
        }
        return saveOrUpdate(goodsCommonBody);
    }
}
