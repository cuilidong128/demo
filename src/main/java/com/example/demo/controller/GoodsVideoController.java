package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.GoodsVideo;
import com.example.demo.service.GoodsVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品SPU关联音频/视频表控制器
 */
@Slf4j
@RestController
@RequestMapping("/goods-video")
@RequiredArgsConstructor
public class GoodsVideoController {

    private final GoodsVideoService goodsVideoService;

    /**
     * 获取所有视频列表
     */
    @GetMapping("/list")
    public ApiResponse<List<GoodsVideo>> getList() {
        log.info("获取所有视频列表");
        try {
            List<GoodsVideo> list = goodsVideoService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取视频列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取视频列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<GoodsVideo>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取视频列表");
        try {
            Page<GoodsVideo> page = new Page<>(pageNum, pageSize);
            Page<GoodsVideo> result = goodsVideoService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取视频列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据商品SPU ID获取视频列表
     */
    @GetMapping("/common/{commonId}")
    public ApiResponse<List<GoodsVideo>> getByCommonId(@PathVariable Integer commonId) {
        log.info("根据商品SPU ID获取视频列表, commonId: {}", commonId);
        try {
            List<GoodsVideo> list = goodsVideoService.getByCommonId(commonId);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取视频列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取视频
     */
    @GetMapping("/{id}")
    public ApiResponse<GoodsVideo> getById(@PathVariable Integer id) {
        log.info("根据ID获取视频, id: {}", id);
        try {
            GoodsVideo video = goodsVideoService.getById(id);
            if (video != null) {
                return ApiResponse.success("获取成功", video);
            } else {
                return ApiResponse.error(404, "视频不存在");
            }
        } catch (Exception e) {
            log.error("获取视频失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建视频
     */
    @PostMapping("/create")
    public ApiResponse<GoodsVideo> create(@RequestBody GoodsVideo goodsVideo) {
        log.info("创建视频, commonId: {}", goodsVideo.getCommonId());
        try {
            goodsVideoService.save(goodsVideo);
            return ApiResponse.success("创建成功", goodsVideo);
        } catch (Exception e) {
            log.error("创建视频失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建视频
     */
    @PostMapping("/batch-create")
    public ApiResponse<List<GoodsVideo>> batchCreate(@RequestBody List<GoodsVideo> goodsVideos) {
        log.info("批量创建视频, 数量: {}", goodsVideos.size());
        try {
            goodsVideoService.saveBatch(goodsVideos);
            return ApiResponse.success("批量创建成功", goodsVideos);
        } catch (Exception e) {
            log.error("批量创建视频失败: {}", e.getMessage());
            return ApiResponse.error("批量创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新视频
     */
    @PutMapping("/update")
    public ApiResponse<GoodsVideo> update(@RequestBody GoodsVideo goodsVideo) {
        log.info("更新视频, id: {}", goodsVideo.getVideoId());
        try {
            goodsVideoService.updateById(goodsVideo);
            return ApiResponse.success("更新成功", goodsVideo);
        } catch (Exception e) {
            log.error("更新视频失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除视频, id: {}", id);
        try {
            goodsVideoService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除视频失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除视频
     */
    @DeleteMapping("/batch")
    public ApiResponse<Void> batchDelete(@RequestParam List<Integer> ids) {
        log.info("批量删除视频, ids: {}", ids);
        try {
            goodsVideoService.removeByIds(ids);
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            log.error("批量删除视频失败: {}", e.getMessage());
            return ApiResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 设置默认视频
     */
    @PutMapping("/set-default/{videoId}")
    public ApiResponse<Void> setDefaultVideo(
            @PathVariable Integer videoId,
            @RequestParam Integer commonId) {
        log.info("设置默认视频, videoId: {}, commonId: {}", videoId, commonId);
        try {
            boolean result = goodsVideoService.setDefaultVideo(videoId, commonId);
            if (result) {
                return ApiResponse.success("设置默认视频成功", null);
            } else {
                return ApiResponse.error("设置默认视频失败");
            }
        } catch (Exception e) {
            log.error("设置默认视频失败: {}", e.getMessage());
            return ApiResponse.error("设置失败: " + e.getMessage());
        }
    }
}
