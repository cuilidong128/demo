package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.config.UploadConfig;
import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Album;
import com.example.demo.entity.AlbumFiles;
import com.example.demo.service.AlbumFilesService;
import com.example.demo.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 相册文件查询参数
 */
class AlbumFilesQuery {
    private Integer albumId;
    private Integer isSystem;
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 20;

    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }
    public Integer getIsSystem() { return isSystem; }
    public void setIsSystem(Integer isSystem) { this.isSystem = isSystem; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}

/**
 * 批量删除请求
 */
class BatchDeleteRequest {
    private List<Integer> fileIds;
    public List<Integer> getFileIds() { return fileIds; }
    public void setFileIds(List<Integer> fileIds) { this.fileIds = fileIds; }
}

/**
 * 批量转移请求
 */
class BatchTransferRequest {
    private List<Integer> fileIds;
    private Integer targetAlbumId;
    public List<Integer> getFileIds() { return fileIds; }
    public void setFileIds(List<Integer> fileIds) { this.fileIds = fileIds; }
    public Integer getTargetAlbumId() { return targetAlbumId; }
    public void setTargetAlbumId(Integer targetAlbumId) { this.targetAlbumId = targetAlbumId; }
}

/**
 * 重命名请求
 */
class RenameRequest {
    private Integer fileId;
    private String newName;
    public Integer getFileId() { return fileId; }
    public void setFileId(Integer fileId) { this.fileId = fileId; }
    public String getNewName() { return newName; }
    public void setNewName(String newName) { this.newName = newName; }
}

/**
 * 相册控制器
 */
@Slf4j
@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumFilesService albumFilesService;
    private final UploadConfig uploadConfig;

    /**
     * 获取所有相册列表（扁平结构）
     */
    @GetMapping("/list")
    public ApiResponse<List<Album>> getList() {
        log.info("获取所有相册列表");
        try {
            List<Album> list = albumService.list();
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取相册列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取相册树形结构
     */
    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> getTree() {
        log.info("获取相册树形结构");
        try {
            List<Album> list = albumService.list();
            List<Map<String, Object>> tree = buildTree(list);
            return ApiResponse.success("获取成功", tree);
        } catch (Exception e) {
            log.error("获取相册树失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取相册列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<Album>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页获取相册列表");
        try {
            Page<Album> page = new Page<>(pageNum, pageSize);
            Page<Album> result = albumService.page(page);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取相册列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取相册
     */
    @GetMapping("/{id}")
    public ApiResponse<Album> getById(@PathVariable Integer id) {
        log.info("根据ID获取相册, id: {}", id);
        try {
            Album album = albumService.getById(id);
            if (album != null) {
                return ApiResponse.success("获取成功", album);
            } else {
                return ApiResponse.error(404, "相册不存在");
            }
        } catch (Exception e) {
            log.error("获取相册失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 创建相册
     */
    @PostMapping("/create")
    public ApiResponse<Album> create(@RequestBody Album album) {
        log.info("创建相册: {}", album.getAlbumName());
        try {
            if (album.getParentId() == null) {
                album.setParentId(0);
            }
            albumService.save(album);
            return ApiResponse.success("创建成功", album);
        } catch (Exception e) {
            log.error("创建相册失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新相册
     */
    @PutMapping("/update")
    public ApiResponse<Album> update(@RequestBody Album album) {
        log.info("更新相册: {}", album.getAlbumId());
        try {
            albumService.updateById(album);
            return ApiResponse.success("更新成功", album);
        } catch (Exception e) {
            log.error("更新相册失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除相册（同时删除子相册和文件）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        log.info("删除相册: {}", id);
        try {
            // 删除子相册和文件
            deleteChildren(id);
            // 删除相册中的文件
            QueryWrapper<AlbumFiles> fileWrapper = new QueryWrapper<>();
            fileWrapper.eq("album_id", id);
            albumFilesService.remove(fileWrapper);
            // 删除相册
            albumService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除相册失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 递归删除子相册
     */
    private void deleteChildren(Integer parentId) {
        QueryWrapper<Album> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<Album> children = albumService.list(wrapper);
        for (Album child : children) {
            deleteChildren(child.getAlbumId());
            // 删除子相册中的文件
            QueryWrapper<AlbumFiles> fileWrapper = new QueryWrapper<>();
            fileWrapper.eq("album_id", child.getAlbumId());
            albumFilesService.remove(fileWrapper);
            albumService.removeById(child.getAlbumId());
        }
    }

    // ==================== 相册文件管理 ====================

    /**
     * 根据相册ID获取文件列表
     * 返回的文件URL为完整绝对路径
     */
    @GetMapping("/files/list/{albumId}")
    public ApiResponse<List<AlbumFiles>> getFilesByAlbumId(@PathVariable Integer albumId) {
        log.info("根据相册ID获取文件列表, albumId: {}", albumId);
        try {
            QueryWrapper<AlbumFiles> wrapper = new QueryWrapper<>();
            wrapper.eq("album_id", albumId);
            List<AlbumFiles> list = albumFilesService.list(wrapper);
            
            // 将相对路径转换为完整URL
            String urlPrefix = uploadConfig.getLocal().getUrlPrefix();
            for (AlbumFiles file : list) {
                String filesName = file.getFilesName();
                if (filesName != null && !filesName.isEmpty()) {
                    // 如果已经是完整URL，不处理
                    if (!filesName.startsWith("http://") && !filesName.startsWith("https://")) {
                        file.setFilesName(urlPrefix + "/" + filesName);
                    }
                }
            }
            
            return ApiResponse.success("获取成功", list);
        } catch (Exception e) {
            log.error("获取文件列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取文件列表（支持按系统图片筛选和搜索）
     */
    @GetMapping("/files/page")
    public ApiResponse<Page<AlbumFiles>> getFilesPage(AlbumFilesQuery query) {
        log.info("分页获取文件列表, albumId: {}, isSystem: {}, keyword: {}", 
                query.getAlbumId(), query.getIsSystem(), query.getKeyword());
        try {
            Page<AlbumFiles> page = new Page<>(query.getPageNum(), query.getPageSize());
            QueryWrapper<AlbumFiles> wrapper = new QueryWrapper<>();
            
            // 按相册ID筛选
            if (query.getAlbumId() != null) {
                wrapper.eq("album_id", query.getAlbumId());
            }
            
            // 按系统图片筛选
            if (query.getIsSystem() != null) {
                wrapper.eq("is_system", query.getIsSystem());
            }
            
            // 按关键词搜索（文件名）
            if (query.getKeyword() != null && !query.getKeyword().trim().isEmpty()) {
                wrapper.like("original_name", query.getKeyword().trim());
            }
            
            // 默认按上传时间倒序
            wrapper.orderByDesc("upload_time");
            
            Page<AlbumFiles> result = albumFilesService.page(page, wrapper);
            
            // 将相对路径转换为完整URL
            String urlPrefix = uploadConfig.getLocal().getUrlPrefix();
            for (AlbumFiles file : result.getRecords()) {
                String filesName = file.getFilesName();
                if (filesName != null && !filesName.isEmpty()) {
                    if (!filesName.startsWith("http://") && !filesName.startsWith("https://")) {
                        file.setFilesName(urlPrefix + "/" + filesName);
                    }
                }
            }
            
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            log.error("获取文件列表失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除文件
     */
    @PostMapping("/files/batch-delete")
    public ApiResponse<Void> batchDeleteFiles(@RequestBody BatchDeleteRequest request) {
        log.info("批量删除文件, fileIds: {}", request.getFileIds());
        try {
            if (request.getFileIds() == null || request.getFileIds().isEmpty()) {
                return ApiResponse.error("请选择要删除的文件");
            }
            albumFilesService.removeByIds(request.getFileIds());
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("批量删除文件失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量转移文件
     */
    @PostMapping("/files/batch-transfer")
    public ApiResponse<Void> batchTransferFiles(@RequestBody BatchTransferRequest request) {
        log.info("批量转移文件, fileIds: {}, targetAlbumId: {}", 
                request.getFileIds(), request.getTargetAlbumId());
        try {
            if (request.getFileIds() == null || request.getFileIds().isEmpty()) {
                return ApiResponse.error("请选择要转移的文件");
            }
            if (request.getTargetAlbumId() == null) {
                return ApiResponse.error("请选择目标相册");
            }
            
            // 批量更新相册ID
            for (Integer fileId : request.getFileIds()) {
                AlbumFiles file = albumFilesService.getById(fileId);
                if (file != null) {
                    file.setAlbumId(request.getTargetAlbumId());
                    albumFilesService.updateById(file);
                }
            }
            return ApiResponse.success("转移成功", null);
        } catch (Exception e) {
            log.error("批量转移文件失败: {}", e.getMessage());
            return ApiResponse.error("转移失败: " + e.getMessage());
        }
    }

    /**
     * 重命名文件
     */
    @PostMapping("/files/rename")
    public ApiResponse<AlbumFiles> renameFile(@RequestBody RenameRequest request) {
        log.info("重命名文件, fileId: {}, newName: {}", request.getFileId(), request.getNewName());
        try {
            if (request.getFileId() == null) {
                return ApiResponse.error("文件ID不能为空");
            }
            if (request.getNewName() == null || request.getNewName().trim().isEmpty()) {
                return ApiResponse.error("新文件名不能为空");
            }
            
            AlbumFiles file = albumFilesService.getById(request.getFileId());
            if (file == null) {
                return ApiResponse.error(404, "文件不存在");
            }
            
            file.setOriginalName(request.getNewName().trim());
            albumFilesService.updateById(file);
            return ApiResponse.success("重命名成功", file);
        } catch (Exception e) {
            log.error("重命名文件失败: {}", e.getMessage());
            return ApiResponse.error("重命名失败: " + e.getMessage());
        }
    }

    /**
     * 创建文件记录
     */
    @PostMapping("/files/create")
    public ApiResponse<AlbumFiles> createFile(@RequestBody AlbumFiles file) {
        log.info("创建文件记录: {}", file.getOriginalName());
        try {
            albumFilesService.save(file);
            return ApiResponse.success("创建成功", file);
        } catch (Exception e) {
            log.error("创建文件记录失败: {}", e.getMessage());
            return ApiResponse.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件记录
     */
    @PutMapping("/files/update")
    public ApiResponse<AlbumFiles> updateFile(@RequestBody AlbumFiles file) {
        log.info("更新文件记录: {}", file.getFilesId());
        try {
            albumFilesService.updateById(file);
            return ApiResponse.success("更新成功", file);
        } catch (Exception e) {
            log.error("更新文件记录失败: {}", e.getMessage());
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件记录
     */
    @DeleteMapping("/files/{id}")
    public ApiResponse<Void> deleteFile(@PathVariable Integer id) {
        log.info("删除文件记录: {}", id);
        try {
            albumFilesService.removeById(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除文件记录失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 构建相册树
     */
    private List<Map<String, Object>> buildTree(List<Album> albums) {
        if (albums == null || albums.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> list = albums.stream().map(album -> {
            Map<String, Object> map = new HashMap<>();
            map.put("albumId", album.getAlbumId());
            map.put("albumName", album.getAlbumName());
            map.put("parentId", album.getParentId());
            map.put("storeId", album.getStoreId());
            map.put("createTime", album.getCreateTime());
            map.put("updateTime", album.getUpdateTime());
            map.put("children", new ArrayList<Map<String, Object>>());
            return map;
        }).collect(Collectors.toList());

        Map<Integer, Map<String, Object>> map = list.stream()
                .collect(Collectors.toMap(m -> (Integer) m.get("albumId"), m -> m));

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
