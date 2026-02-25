package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.FileUploadResult;
import com.example.demo.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传控制器
 * 支持本地上传和阿里云OSS上传
 */
@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 单文件上传
     *
     * @param file       文件
     * @param uploadType 上传类型：local-本地，oss-阿里云，不传则默认本地
     * @param directory  目标目录
     * @return 文件上传结果（包含相对路径和完整URL）
     */
    @PostMapping("/upload")
    public ApiResponse<FileUploadResult> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadType", required = false, defaultValue = "local") String uploadType,
            @RequestParam(value = "directory", required = false) String directory) {
        
        log.info("文件上传请求, uploadType: {}, directory: {}, fileName: {}", 
                uploadType, directory, file.getOriginalFilename());
        
        try {
            FileUploadResult result = fileUploadService.upload(file, uploadType, directory);
            return ApiResponse.success("上传成功", result);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量文件上传
     *
     * @param files      文件列表
     * @param uploadType 上传类型
     * @param directory  目标目录
     * @return 文件上传结果列表
     */
    @PostMapping("/upload/batch")
    public ApiResponse<List<FileUploadResult>> uploadBatch(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "uploadType", required = false, defaultValue = "local") String uploadType,
            @RequestParam(value = "directory", required = false) String directory) {
        
        log.info("批量文件上传请求, uploadType: {}, directory: {}, 文件数量: {}", 
                uploadType, directory, files.length);
        
        List<FileUploadResult> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                FileUploadResult result = fileUploadService.upload(file, uploadType, directory);
                results.add(result);
            } catch (Exception e) {
                log.error("文件上传失败: {}, 错误: {}", file.getOriginalFilename(), e.getMessage());
                errors.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            return ApiResponse.success("部分文件上传失败: " + String.join(", ", errors), results);
        }
        
        return ApiResponse.success("上传成功", results);
    }

    /**
     * 删除文件
     *
     * @param fileUrl    文件URL
     * @param uploadType 上传类型
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    public ApiResponse<Boolean> delete(
            @RequestParam("fileUrl") String fileUrl,
            @RequestParam(value = "uploadType", required = false) String uploadType) {
        
        log.info("删除文件请求, fileUrl: {}", fileUrl);
        
        try {
            // 如果没有指定上传类型，自动识别
            if (uploadType == null || uploadType.isEmpty()) {
                uploadType = fileUploadService.getUploadType(fileUrl);
            }
            
            boolean result = fileUploadService.delete(fileUrl, uploadType);
            if (result) {
                return ApiResponse.success("删除成功", true);
            } else {
                return ApiResponse.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件上传类型
     *
     * @param fileUrl 文件URL
     * @return 上传类型
     */
    @GetMapping("/type")
    public ApiResponse<String> getUploadType(@RequestParam("fileUrl") String fileUrl) {
        try {
            String type = fileUploadService.getUploadType(fileUrl);
            return ApiResponse.success("获取成功", type);
        } catch (Exception e) {
            log.error("获取上传类型失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 根据相对路径获取完整URL
     *
     * @param relativePath 相对路径
     * @return 完整URL
     */
    @GetMapping("/full-url")
    public ApiResponse<String> getFullUrl(@RequestParam("relativePath") String relativePath) {
        try {
            String fullUrl = fileUploadService.getFullUrl(relativePath);
            return ApiResponse.success("获取成功", fullUrl);
        } catch (Exception e) {
            log.error("获取完整URL失败: {}", e.getMessage());
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}
