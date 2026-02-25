package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.GoodsSpecImage;
import com.example.demo.service.GoodsSpecImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品SKU管理图片表控制器
 */
@Slf4j
@RestController
@RequestMapping("/goods-spec-image")
@RequiredArgsConstructor
public class GoodsSpecImageController {

    private final GoodsSpecImageService goodsSpecImageService;

    /**
     * 获取所有规格图片列表
     */
    @GetMapping("/list")
    public ApiResponse<List<GoodsSpecImage>> getList() {
        log.info("获取所有规格图片列表");
        try {
            List<GoodsSpecImage> list = goodsSpecImageService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取规格图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取规格图片列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<GoodsSpecImage>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取规格图片列表");
        try {
            Page<GoodsSpecImage> page = new Page<>(pageNum, pageSize);
            Page<GoodsSpecImage> result = goodsSpecImageService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取规格图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品SPU ID获取规格图片列表
     */
    @GetMapping("/common/{commonId}")
    public ApiResponse<List<GoodsSpecImage>> getByCommonId(@PathVariable Integer commonId) {
        log.info("根据商品SPU ID获取规格图片列表, commonId: {}", commonId);
        try {
            List<GoodsSpecImage> list = goodsSpecImageService.getByCommonId(commonId);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取规格图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取规格图片
     */
    @GetMapping("/{id}")
    public ApiResponse<GoodsSpecImage> getById(@PathVariable Integer id) {
        log.info("根据ID获取规格图片, id: {}", id);
        try {
            GoodsSpecImage image = goodsSpecImageService.getById(id);
            if (image != null) {
                return ApiResponse.success("获取成功", image);
            } else {
                return ApiResponse.error(404, "规格图片不存在");
            }
        } catch (Exception e) {
            log.error("获取规格图片失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建规格图片
     */
    @PostMapping("/create")
    public ApiResponse<GoodsSpecImage> create(@RequestBody GoodsSpecImage goodsSpecImage) {
        log.info("创建规格图片, commonId: {}", goodsSpecImage.getCommonId());
        try {
            goodsSpecImageService.save(goodsSpecImage);
            return ApiResponse.success("创建成功", goodsSpecImage);
        } catch (Exception e) {
            log.error("创建规格图片失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建规格图片
     */
    @PostMapping("/batch-create")
    public ApiResponse<List<GoodsSpecImage>> batchCreate(@RequestBody List<GoodsSpecImage> goodsSpecImages) {
        log.info("批量创建规格图片, 数量: {}", goodsSpecImages.size());
        try {
            goodsSpecImageService.saveBatch(goodsSpecImages);
            return ApiResponse.success("批量创建成功", goodsSpecImages);
        } catch (Exception e) {
            log.error("批量创建规格图片失败: {}", e.getMessage());
            return ApiResponse.error("批量创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新规格图片
     */
    @PutMapping("/update")
    public ApiResponse<GoodsSpecImage> update(@RequestBody GoodsSpecImage goodsSpecImage) {
        log.info("更新规格图片, id: {}", goodsSpecImage.getSpecImageId());
        try {
            goodsSpecImageService.updateById(goodsSpecImage);
            return ApiResponse.success("更新成功", goodsSpecImage);
        } catch (Exception e) {
            log.error("更新规格图片失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除规格图片
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除规格图片, id: {}", id);
        try {
            goodsSpecImageService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除规格图片失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除规格图片
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDelete(@RequestParam List<Integer> ids) {
        log.info("批量删除规格图片, ids: {}", ids);
        try {
            goodsSpecImageService.removeByIds(ids);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            log.error("批量删除规格图片失败: {}", e.getMessage());
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }
}
