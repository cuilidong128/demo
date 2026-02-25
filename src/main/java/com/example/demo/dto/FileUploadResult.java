package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果DTO
 * 包含相对路径和完整URL
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResult {

    /**
     * 文件相对路径（用于数据库存储）
     * 格式：directory/yyyy/MM/dd/filename.ext
     */
    private String relativePath;

    /**
     * 文件完整访问URL（用于前端展示）
     * 格式：http://localhost:8080/uploads/directory/yyyy/MM/dd/filename.ext
     */
    private String fullUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}
