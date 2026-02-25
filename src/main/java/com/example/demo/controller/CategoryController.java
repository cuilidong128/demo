package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品类目控制器
 */
@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有类目列表（扁平结构）
     */
    @GetMapping("/list")
    public ApiResponse<List<Category>> getList() {
        log.info("获取所有类目列表");
        try {
            List<Category> list = categoryService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取类目列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取类目树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getTree() {
        log.info("获取类目树形结构");
        try {
            List<Category> list = categoryService.list();
            List<Map<String, Object>> tree = buildTree(list);
            return ApiResponse.success("获取成功", tree);
        } catch (Exception e) {
            log.error("获取类目树失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取类目列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<Category>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取类目列表, pageNum: {}, pageSize: {}", pageNum, pageSize);
        try {
            Page<Category> page = new Page<>(pageNum, pageSize);
            Page<Category> result = categoryService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取类目列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取类目
     */
    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Integer id) {
        log.info("根据ID获取类目, id: {}", id);
        try {
            Category category = categoryService.getById(id);
            if (category != null) {
                return ApiResponse.success("获取成功", category);
            } else {
                return ApiResponse.error(404, "类目不存在");
            }
        } catch (Exception e) {
            log.error("获取类目失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据父级ID获取子类目
     */
    @GetMapping("/parent/{parentId}")
    public ApiResponse<List<Category>> getByParentId(@PathVariable Integer parentId) {
        log.info("根据父级ID获取子类目, parentId: {}", parentId);
        try {
            QueryWrapper<Category> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", parentId);
            List<Category> list = categoryService.list(wrapper);
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取子类目失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建类目
     */
    @PostMapping("/create")
    public ApiResponse<Category> create(@RequestBody Category category) {
        log.info("创建类目: {}", category.getCategoryName());
        try {
            if (category.getParentId() == null) {
                category.setParentId(0);
            }
            if (category.getDeep() == null) {
                category.setDeep(1);
            }
            categoryService.save(category);
            return ApiResponse.success("创建成功", category);
        } catch (Exception e) {
            log.error("创建类目失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新类目
     */
    @PutMapping("/update")
    public ApiResponse<Category> update(@RequestBody Category category) {
        log.info("更新类目: {}", category.getCategoryId());
        try {
            categoryService.updateById(category);
            return ApiResponse.success("更新成功", category);
        } catch (Exception e) {
            log.error("更新类目失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除类目（同时删除子类目）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除类目: {}", id);
        try {
            deleteChildren(id);
            categoryService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除类目失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 递归删除子类目
     */
    private void deleteChildren(Integer parentId) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<Category> children = categoryService.list(wrapper);
        for (Category child : children) {
            deleteChildren(child.getCategoryId());
            categoryService.removeById(child.getCategoryId());
        }
    }

    /**
     * 构建类目树
     */
    private List<Map<String, Object>> buildTree(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> list = categories.stream().map(cat -> {
            Map<String, Object> map = new HashMap<>();
            map.put("categoryId", cat.getCategoryId());
            map.put("categoryName", cat.getCategoryName());
            map.put("appImage", cat.getAppImage());
            map.put("categorySort", cat.getCategorySort());
            map.put("deep", cat.getDeep());
            map.put("parentId", cat.getParentId());
            map.put("createTime", cat.getCreateTime());
            map.put("updateTime", cat.getUpdateTime());
            map.put("children", new ArrayList<Map<String, Object>>());
            return map;
        }).collect(Collectors.toList());

        Map<Integer, Map<String, Object>> map = list.stream()
                .collect(Collectors.toMap(m -> (Integer) m.get("categoryId"), m -> m));

        List<Map<String, Object>> root = new ArrayList<>();
        for (Map<String, Object> item : list) {
            Integer parentId = (Integer) item.get("parentId");
            if (parentId == null || parentId == 0 || !map.containsKey(parentId)) {
                root.add(item);
            } else {
                Map<String, Object> parent = map.get(parentId);
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(item);
                }
            }
        }
        return root;
    }
}
