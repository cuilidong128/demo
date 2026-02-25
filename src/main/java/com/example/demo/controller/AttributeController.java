package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Attribute;
import com.example.demo.entity.AttributeValue;
import com.example.demo.service.AttributeService;
import com.example.demo.service.AttributeValueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品属性控制器
 */
@Slf4j
@RestController
@RequestMapping("/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;
    private final AttributeValueService attributeValueService;

    /**
     * 获取所有属性列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Attribute>> getList() {
        log.info("获取所有属性列表");
        try {
            List<Attribute> list = attributeService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取属性列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取属性列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<Attribute>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取属性列表");
        try {
            Page<Attribute> page = new Page<>(pageNum, pageSize);
            Page<Attribute> result = attributeService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取属性列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据类目ID获取属性列表
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Attribute>> getByCategoryId(@PathVariable Integer categoryId) {
        log.info("根据类目ID获取属性列表, categoryId: {}", categoryId);
        try {
            QueryWrapper<Attribute> wrapper = new QueryWrapper<>();
            wrapper.eq("category_id", categoryId);
            List<Attribute> list = attributeService.list(wrapper);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取属性列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取属性
     */
    @GetMapping("/{id}")
    public ApiResponse<Attribute> getById(@PathVariable Integer id) {
        log.info("根据ID获取属性, id: {}", id);
        try {
            Attribute attribute = attributeService.getById(id);
            if (attribute != null) {
                return ApiResponse.success("获取成功", attribute);
            } else {
                return ApiResponse.error(404, "属性不存在");
            }
        } catch (Exception e) {
            log.error("获取属性失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建属性
     */
    @PostMapping("/create")
    public ApiResponse<Attribute> create(@RequestBody Attribute attribute) {
        log.info("创建属性: {}", attribute.getAttributeName());
        try {
            attributeService.save(attribute);
            return ApiResponse.success("创建成功", attribute);
        } catch (Exception e) {
            log.error("创建属性失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新属性
     */
    @PutMapping("/update")
    public ApiResponse<Attribute> update(@RequestBody Attribute attribute) {
        log.info("更新属性: {}", attribute.getAttributeId());
        try {
            attributeService.updateById(attribute);
            return ApiResponse.success("更新成功", attribute);
        } catch (Exception e) {
            log.error("更新属性失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除属性（同时删除属性值）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除属性: {}", id);
        try {
            // 删除属性值
            QueryWrapper<AttributeValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attribute_id", id);
            attributeValueService.remove(wrapper);
            // 删除属性
            attributeService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除属性失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    // ==================== 属性值管理 ====================

    /**
     * 根据属性ID获取属性值列表
     */
    @GetMapping("/value/list/{attributeId}")
    public ApiResponse<List<AttributeValue>> getValuesByAttributeId(@PathVariable Integer attributeId) {
        log.info("根据属性ID获取属性值列表, attributeId: {}", attributeId);
        try {
            QueryWrapper<AttributeValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attribute_id", attributeId);
            List<AttributeValue> list = attributeValueService.list(wrapper);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取属性值列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建属性值
     */
    @PostMapping("/value/create")
    public ApiResponse<AttributeValue> createValue(@RequestBody AttributeValue value) {
        log.info("创建属性值: {}", value.getAttributeValueName());
        try {
            attributeValueService.save(value);
            return ApiResponse.success("创建成功", value);
        } catch (Exception e) {
            log.error("创建属性值失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新属性值
     */
    @PutMapping("/value/update")
    public ApiResponse<AttributeValue> updateValue(@RequestBody AttributeValue value) {
        log.info("更新属性值: {}", value.getAttributeValueId());
        try {
            attributeValueService.updateById(value);
            return ApiResponse.success("更新成功", value);
        } catch (Exception e) {
            log.error("更新属性值失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除属性值
     */
    @DeleteMapping("/value/{id}")
    public ApiResponse<Void> deleteValue(@PathVariable Integer id) {
        log.info("删除属性值: {}", id);
        try {
            attributeValueService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除属性值失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
}
