package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.GoodsImage;
import com.example.demo.service.GoodsImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品图片表控制器
 * 支持本地文件上传和阿里云OSS上传
 */
@Slf4j
@RestController
@RequestMapping("/goods-image")
@RequiredArgsConstructor
public class GoodsImageController {

    private final GoodsImageService goodsImageService;

    /**
     * 获取所有图片列表
     */
    @GetMapping("/list")
    public ApiResponse<List<GoodsImage>> getList() {
        log.info("获取所有图片列表");
        try {
            List<GoodsImage> list = goodsImageService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取图片列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<GoodsImage>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取图片列表");
        try {
            Page<GoodsImage> page = new Page<>(pageNum, pageSize);
            Page<GoodsImage> result = goodsImageService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品SPU ID获取图片列表
     */
    @GetMapping("/common/{commonId}")
    public ApiResponse<List<GoodsImage>> getByCommonId(@PathVariable Integer commonId) {
        log.info("根据商品SPU ID获取图片列表, commonId: {}", commonId);
        try {
            List<GoodsImage> list = goodsImageService.getByCommonId(commonId);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取图片列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取图片
     */
    @GetMapping("/{id}")
    public ApiResponse<GoodsImage> getById(@PathVariable Integer id) {
        log.info("根据ID获取图片, id: {}", id);
        try {
            GoodsImage image = goodsImageService.getById(id);
            if (image != null) {
                return ApiResponse.success("获取成功", image);
            } else {
                return ApiResponse.error(404, "图片不存在");
            }
        } catch (Exception e) {
            log.error("获取图片失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建图片
     */
    @PostMapping("/create")
    public ApiResponse<GoodsImage> create(@RequestBody GoodsImage goodsImage) {
        log.info("创建图片, commonId: {}", goodsImage.getCommonId());
        try {
            goodsImageService.save(goodsImage);
            return ApiResponse.success("创建成功", goodsImage);
        } catch (Exception e) {
            log.error("创建图片失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建图片
     */
    @PostMapping("/batch-create")
    public ApiResponse<List<GoodsImage>> batchCreate(@RequestBody List<GoodsImage> goodsImages) {
        log.info("批量创建图片, 数量: {}", goodsImages.size());
        try {
            goodsImageService.saveBatch(goodsImages);
            return ApiResponse.success("批量创建成功", goodsImages);
        } catch (Exception e) {
            log.error("批量创建图片失败: {}", e.getMessage());
            return ApiResponse.error("批量创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新图片
     */
    @PutMapping("/update")
    public ApiResponse<GoodsImage> update(@RequestBody GoodsImage goodsImage) {
        log.info("更新图片, id: {}", goodsImage.getImageId());
        try {
            goodsImageService.updateById(goodsImage);
            return ApiResponse.success("更新成功", goodsImage);
        } catch (Exception e) {
            log.error("更新图片失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除图片, id: {}", id);
        try {
            goodsImageService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除图片失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除图片
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDelete(@RequestParam List<Integer> ids) {
        log.info("批量删除图片, ids: {}", ids);
        try {
            goodsImageService.removeByIds(ids);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            log.error("批量删除图片失败: {}", e.getMessage());
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认图片
     */
    @PutMapping("/set-default/{imageId}")
    public ApiResponse<Void> setDefaultImage(
            @PathVariable Integer imageId,
            @RequestParam Integer commonId) {
        log.info("设置默认图片, imageId: {}, commonId: {}", imageId, commonId);
        try {
            boolean result = goodsImageService.setDefaultImage(imageId, commonId);
            if (result) {
                return ApiResponse.success("设置默认图片成功", null);
            } else {
                return ApiResponse.error("设置默认图片失败");
            }
        } catch (Exception e) {
            log.error("设置默认图片失败: {}", e.getMessage());
            return ApiResponse.error("设置失败: " + e.getMessage());
        }
    }
}
