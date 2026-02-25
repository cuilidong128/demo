package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.GoodsCommonBody;
import com.example.demo.service.GoodsCommonBodyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 商品详情表控制器
 * 支持富文本编辑器编辑商品详情
 */
@Slf4j
@RestController
@RequestMapping("/goods-common-body")
@RequiredArgsConstructor
public class GoodsCommonBodyController {

    private final GoodsCommonBodyService goodsCommonBodyService;

    /**
     * 根据商品SPU ID获取详情
     */
    @GetMapping("/{commonId}")
    public ApiResponse<GoodsCommonBody> getByCommonId(@PathVariable Integer commonId) {
        log.info("获取商品详情, commonId: {}", commonId);
        try {
            GoodsCommonBody body = goodsCommonBodyService.getByCommonId(commonId);
            if (body != null) {
                return ApiResponse.success("获取成功", body);
            } else {
                return ApiResponse.error(404, "商品详情不存在");
            }
        } catch (Exception e) {
            log.error("获取商品详情失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建商品详情
     */
    @PostMapping("/create")
    public ApiResponse<GoodsCommonBody> create(@RequestBody GoodsCommonBody goodsCommonBody) {
        log.info("创建商品详情, commonId: {}", goodsCommonBody.getCommonId());
        try {
            if (goodsCommonBody.getCommonId() == null) {
                return ApiResponse.error("商品SPU ID不能为空");
            }
            goodsCommonBodyService.save(goodsCommonBody);
            return ApiResponse.success("创建成功", goodsCommonBody);
        } catch (Exception e) {
            log.error("创建商品详情失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品详情
     */
    @PutMapping("/update")
    public ApiResponse<GoodsCommonBody> update(@RequestBody GoodsCommonBody goodsCommonBody) {
        log.info("更新商品详情, commonId: {}", goodsCommonBody.getCommonId());
        try {
            if (goodsCommonBody.getCommonId() == null) {
                return ApiResponse.error("商品SPU ID不能为空");
            }
            goodsCommonBodyService.updateById(goodsCommonBody);
            return ApiResponse.success("更新成功", goodsCommonBody);
        } catch (Exception e) {
            log.error("更新商品详情失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 保存或更新商品详情
     */
    @PostMapping("/save-or-update")
    public ApiResponse<GoodsCommonBody> saveOrUpdate(@RequestBody GoodsCommonBody goodsCommonBody) {
        log.info("保存或更新商品详情, commonId: {}", goodsCommonBody.getCommonId());
        try {
            if (goodsCommonBody.getCommonId() == null) {
                return ApiResponse.error("商品SPU ID不能为空");
            }
            goodsCommonBodyService.saveOrUpdateByCommonId(goodsCommonBody);
            return ApiResponse.success("保存成功", goodsCommonBody);
        } catch (Exception e) {
            log.error("保存商品详情失败: {}", e.getMessage());
            return ApiResponse.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 删除商品详情
     */
    @DeleteMapping("/{commonId}")
    public ApiResponse<Void> delete(@PathVariable Integer commonId) {
        log.info("删除商品详情, commonId: {}", commonId);
        try {
            goodsCommonBodyService.removeById(commonId);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除商品详情失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
}
