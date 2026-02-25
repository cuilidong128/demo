package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.GoodsCommonDTO;
import com.example.demo.dto.GoodsCreateRequest;
import com.example.demo.entity.GoodsCommonBody;
import com.example.demo.service.GoodsCommonBodyService;
import com.example.demo.entity.Goods;
import com.example.demo.entity.GoodsCommon;
import com.example.demo.service.GoodsCommonService;
import com.example.demo.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品SPU控制器
 */
@Slf4j
@RestController
@RequestMapping("/goods-common")
@RequiredArgsConstructor
public class GoodsCommonController {

    private final GoodsCommonService goodsCommonService;
    private final GoodsService goodsService;
    private final GoodsCommonBodyService goodsCommonBodyService;

    /**
     * 获取所有商品SPU列表（包含SKU）
     */
    @GetMapping("/list")
    public ApiResponse<List<GoodsCommonDTO>> getList() {
        log.info("获取所有商品SPU列表");
        try {
            List<GoodsCommon> spuList = goodsCommonService.list();
            List<GoodsCommonDTO> dtoList = buildGoodsCommonDTOList(spuList);
            return ApiResponse.success("获取成功", dtoList);
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取商品SPU列表（包含SKU）
     */
    @GetMapping("/page")
    public ApiResponse<Page<GoodsCommonDTO>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取商品SPU列表");
        try {
            Page<GoodsCommon> page = new Page<>(pageNum, pageSize);
            Page<GoodsCommon> result = goodsCommonService.page(page);
            
            // 转换为DTO并填充SKU
            List<GoodsCommonDTO> dtoList = buildGoodsCommonDTOList(result.getRecords());
            
            Page<GoodsCommonDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
            dtoPage.setRecords(dtoList);
            
            return ApiResponse.success("获取成功", dtoPage);
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据类目ID获取商品列表
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<GoodsCommon>> getByCategoryId(@PathVariable Integer categoryId) {
        log.info("根据类目ID获取商品列表, categoryId: {}", categoryId);
        try {
            QueryWrapper<GoodsCommon> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", categoryId);
            List<GoodsCommon> list = goodsCommonService.list(wrapper);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取商品SPU（包含详情）
     */
    @GetMapping("/{id}")
    public ApiResponse<GoodsCommonDTO> getById(@PathVariable Integer id) {
        log.info("根据ID获取商品SPU, id: {}", id);
        try {
            GoodsCommon goods = goodsCommonService.getById(id);
            if (goods != null) {
                GoodsCommonDTO dto = new GoodsCommonDTO();
                BeanUtils.copyProperties(goods, dto);
                
                // 查询SKU列表
                QueryWrapper<Goods> wrapper = new QueryWrapper<>();
                wrapper.eq("common_id", id);
                List<Goods> skuList = goodsService.list(wrapper);
                dto.setSkuList(skuList);
                
                // 查询商品详情
                GoodsCommonBody body = goodsCommonBodyService.getById(id);
                if (body != null) {
                    dto.setGoodsBody(body.getGoodsBody());
                    dto.setMobileBody(body.getMobileBody());
                }
                
                return ApiResponse.success("获取成功", dto);
            } else {
                return ApiResponse.error(404, "商品不存在");
            }
        } catch (Exception e) {
            log.error("获取商品失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/body/{commonId}")
    public ApiResponse<GoodsCommonBody> getBodyByCommonId(@PathVariable Integer commonId) {
        log.info("获取商品详情, commonId: {}", commonId);
        try {
            GoodsCommonBody body = goodsCommonBodyService.getById(commonId);
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
     * 创建商品（包含SPU、SKU列表和商品详情）
     */
    @PostMapping("/create")
    public ApiResponse<GoodsCommon> create(@RequestBody GoodsCreateRequest request) {
        log.info("创建商品: {}", request.getSpu().getGoodsName());
        try {
            // 1. 保存SPU
            GoodsCommon spu = request.getSpu();
            
            // 处理轮播图和主图
            List<String> goodsImages = request.getGoodsImages();
            if (goodsImages != null && !goodsImages.isEmpty()) {
                spu.setGoodsImages(goodsImages);
                // 主图取轮播图第一张
                spu.setGoodsImage(goodsImages.get(0));
                log.info("设置商品主图: {}", goodsImages.get(0));
            }
            
            // 处理商品类目
            if (request.getCategoryId() != null) {
                spu.setCategoryId(request.getCategoryId());
            }
            
            // 如果有SKU列表，使用最后一个SKU的价格和全部SKU库存的和更新SPU
            List<Goods> skuList = request.getSkuList();
            if (skuList != null && !skuList.isEmpty()) {
                // 取最后一个SKU的价格
                Goods lastSku = skuList.get(skuList.size() - 1);
                spu.setGoodsPrice(lastSku.getGoodsPrice());
                if (lastSku.getGoodsMarketPrice() != null) {
                    spu.setGoodsMarketPrice(lastSku.getGoodsMarketPrice());
                }
                
                // 计算所有SKU库存的和
                int totalStorage = skuList.stream()
                        .mapToInt(sku -> sku.getGoodsStorage() != null ? sku.getGoodsStorage() : 0)
                        .sum();
                spu.setGoodsStorage(totalStorage);
                
                log.info("使用最后一个SKU的价格和全部SKU库存的和更新SPU: 价格={}, 库存总和={}", 
                        lastSku.getGoodsPrice(), totalStorage);
            }
            
            goodsCommonService.save(spu);
            Integer commonId = spu.getCommonId();
            
            // 2. 保存SKU列表
            if (skuList != null && !skuList.isEmpty()) {
                for (Goods sku : skuList) {
                    sku.setCommonId(commonId);
                    sku.setGoodsName(spu.getGoodsName());
                }
                goodsService.saveBatch(skuList);
            }
            
            // 3. 保存商品详情（使用saveOrUpdate避免主键冲突）
            if (request.getGoodsBody() != null || request.getMobileBody() != null) {
                GoodsCommonBody body = new GoodsCommonBody();
                body.setCommonId(commonId);
                body.setGoodsBody(request.getGoodsBody());
                body.setMobileBody(request.getMobileBody());
                goodsCommonBodyService.saveOrUpdate(body);
            }
            
            return ApiResponse.success("创建成功", spu);
        } catch (Exception e) {
            log.error("创建商品失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品SPU（包含详情和轮播图）
     */
    @PutMapping("/update")
    public ApiResponse<GoodsCommon> update(@RequestBody GoodsCreateRequest request) {
        log.info("更新商品SPU: {}", request.getSpu().getCommonId());
        try {
            // 1. 更新SPU
            GoodsCommon spu = request.getSpu();
            Integer commonId = spu.getCommonId();
            
            // 处理轮播图和主图
            List<String> goodsImages = request.getGoodsImages();
            if (goodsImages != null && !goodsImages.isEmpty()) {
                spu.setGoodsImages(goodsImages);
                // 主图取轮播图第一张
                spu.setGoodsImage(goodsImages.get(0));
                log.info("更新商品主图: {}", goodsImages.get(0));
            }
            
            // 处理商品类目
            if (request.getCategoryId() != null) {
                spu.setCategoryId(request.getCategoryId());
            }
            
            // 如果有SKU列表，使用最后一个SKU的价格和全部SKU库存的和更新SPU
            List<Goods> skuList = request.getSkuList();
            if (skuList != null && !skuList.isEmpty()) {
                // 取最后一个SKU的价格
                Goods lastSku = skuList.get(skuList.size() - 1);
                spu.setGoodsPrice(lastSku.getGoodsPrice());
                if (lastSku.getGoodsMarketPrice() != null) {
                    spu.setGoodsMarketPrice(lastSku.getGoodsMarketPrice());
                }
                
                // 计算所有SKU库存的和
                int totalStorage = skuList.stream()
                        .mapToInt(sku -> sku.getGoodsStorage() != null ? sku.getGoodsStorage() : 0)
                        .sum();
                spu.setGoodsStorage(totalStorage);
                
                log.info("更新SPU - 使用最后一个SKU的价格和全部SKU库存的和: 价格={}, 库存总和={}", 
                        lastSku.getGoodsPrice(), totalStorage);
            }
            
            goodsCommonService.updateById(spu);
            
            // 2. 更新商品详情
            if (request.getGoodsBody() != null || request.getMobileBody() != null) {
                GoodsCommonBody body = new GoodsCommonBody();
                body.setCommonId(commonId);
                body.setGoodsBody(request.getGoodsBody());
                body.setMobileBody(request.getMobileBody());
                goodsCommonBodyService.saveOrUpdate(body);
            }
            
            return ApiResponse.success("更新成功", spu);
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除商品SPU（同时删除SKU）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除商品SPU: {}", id);
        try {
            // 删除SKU
            QueryWrapper<Goods> wrapper = new QueryWrapper<>();
            wrapper.eq("common_id", id);
            goodsService.remove(wrapper);
            // 删除SPU
            goodsCommonService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    // ==================== SKU管理 ====================

    /**
     * 根据SPU ID获取SKU列表
     */
    @GetMapping("/sku/list/{commonId}")
    public ApiResponse<List<Goods>> getSkusByCommonId(@PathVariable Integer commonId) {
        log.info("根据SPU ID获取SKU列表, commonId: {}", commonId);
        try {
            QueryWrapper<Goods> wrapper = new QueryWrapper<>();
            wrapper.eq("common_id", commonId);
            List<Goods> list = goodsService.list(wrapper);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取SKU列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建 SKU
     */
    @PostMapping("/sku/create")
    public ApiResponse<Goods> createSku(@RequestBody Goods goods) {
        log.info("创建SKU: {}", goods.getGoodsName());
        try {
            goodsService.save(goods);
                
            // 更新SPU的价格和库存（使用第一个SKU）
            updateSpuPriceAndStorage(goods.getCommonId());
                
            return ApiResponse.success("创建成功", goods);
        } catch (Exception e) {
            log.error("创建SKU失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新SKU
     */
    @PutMapping("/sku/update")
    public ApiResponse<Goods> updateSku(@RequestBody Goods goods) {
        log.info("更新SKU: {}", goods.getGoodsId());
        try {
            goodsService.updateById(goods);
            
            // 更新SPU的价格和库存（使用第一个SKU）
            updateSpuPriceAndStorage(goods.getCommonId());
            
            return ApiResponse.success("更新成功", goods);
        } catch (Exception e) {
            log.error("更新SKU失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除SKU
     */
    @DeleteMapping("/sku/{id}")
    public ApiResponse<Void> deleteSku(@PathVariable Integer id) {
        log.info("删除SKU: {}", id);
        try {
            // 先查询SKU信息，获取commonId
            Goods goods = goodsService.getById(id);
            if (goods == null) {
                return ApiResponse.error("商品SKU不存在");
            }
            Integer commonId = goods.getCommonId();
            
            // 删除SKU
            goodsService.removeById(id);
            
            // 更新SPU的价格和库存（使用剩余的第一个SKU）
            updateSpuPriceAndStorage(commonId);
            
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除SKU失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新SPU的价格和库存（使用最后一个SKU的价格和全部SKU库存的和）
     */
    private void updateSpuPriceAndStorage(Integer commonId) {
        if (commonId == null) {
            return;
        }
        
        // 查询该SPU下的所有SKU
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("common_id", commonId);
        List<Goods> skuList = goodsService.list(wrapper);
        
        if (skuList != null && !skuList.isEmpty()) {
            // 获取最后一个SKU的价格
            Goods lastSku = skuList.get(skuList.size() - 1);
            
            // 计算所有SKU库存的和
            int totalStorage = skuList.stream()
                    .mapToInt(sku -> sku.getGoodsStorage() != null ? sku.getGoodsStorage() : 0)
                    .sum();
            
            // 更新SPU
            GoodsCommon spu = goodsCommonService.getById(commonId);
            if (spu != null) {
                spu.setGoodsPrice(lastSku.getGoodsPrice());
                spu.setGoodsStorage(totalStorage);
                if (lastSku.getGoodsMarketPrice() != null) {
                    spu.setGoodsMarketPrice(lastSku.getGoodsMarketPrice());
                }
                goodsCommonService.updateById(spu);
                log.info("更新SPU价格和库存: commonId={}, 价格(最后一个SKU)={}, 库存总和={}", 
                        commonId, lastSku.getGoodsPrice(), totalStorage);
            }
        } else {
            // 如果没有SKU了，将SPU的价格和库存设为0
            GoodsCommon spu = goodsCommonService.getById(commonId);
            if (spu != null) {
                spu.setGoodsPrice(BigDecimal.ZERO);
                spu.setGoodsStorage(0);
                goodsCommonService.updateById(spu);
                log.info("无SKU，重置SPU价格和库存: commonId={}", commonId);
            }
        }
    }

    /**
     * 构建商品DTO列表（包含SKU）
     */
    private List<GoodsCommonDTO> buildGoodsCommonDTOList(List<GoodsCommon> spuList) {
        if (spuList == null || spuList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有SPU的commonId
        List<Integer> commonIds = spuList.stream()
                .map(GoodsCommon::getCommonId)
                .collect(Collectors.toList());
        
        // 批量查询所有SKU
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.in("common_id", commonIds);
        List<Goods> allSkus = goodsService.list(wrapper);
        
        // 按commonId分组
        Map<Integer, List<Goods>> skuMap = allSkus.stream()
                .collect(Collectors.groupingBy(Goods::getCommonId));
        
        // 构建DTO
        List<GoodsCommonDTO> dtoList = new ArrayList<>();
        for (GoodsCommon spu : spuList) {
            GoodsCommonDTO dto = new GoodsCommonDTO();
            BeanUtils.copyProperties(spu, dto);
            dto.setSkuList(skuMap.getOrDefault(spu.getCommonId(), new ArrayList<>()));
            dtoList.add(dto);
        }
        
        return dtoList;
    }
}
