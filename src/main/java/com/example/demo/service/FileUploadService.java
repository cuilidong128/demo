package com.example.demo.service;

import com.example.demo.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件上传服务接口
 * 支持本地上传和阿里云OSS上传
 */
public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param file       文件
     * @param uploadType 上传类型：local-本地，oss-阿里云
     * @return 文件上传结果（包含相对路径和绝对URL）
     */
    FileUploadResult upload(MultipartFile file, String uploadType);

    /**
     * 上传文件（默认使用本地存储）
     *
     * @param file 文件
     * @return 文件上传结果
     */
    FileUploadResult upload(MultipartFile file);

    /**
     * 上传文件到指定目录
     *
     * @param file       文件
     * @param uploadType 上传类型
     * @param directory  目标目录
     * @return 文件上传结果
     */
    FileUploadResult upload(MultipartFile file, String uploadType, String directory);

    /**
     * 上传输入流到指定路径
     *
     * @param inputStream 输入流
     * @param uploadType  上传类型
     * @param fileName    文件名
     * @param directory   目标目录
     * @return 文件上传结果
     */
    FileUploadResult upload(InputStream inputStream, String uploadType, String fileName, String directory);

    /**
     * 删除文件
     *
     * @param fileUrl    文件URL
     * @param uploadType 上传类型
     * @return 是否删除成功
     */
    boolean delete(String fileUrl, String uploadType);

    /**
     * 获取文件上传类型
     *
     * @param fileUrl 文件URL
     * @return 上传类型
     */
    String getUploadType(String fileUrl);

    /**
     * 根据相对路径获取完整URL
     *
     * @param relativePath 相对路径
     * @return 完整URL
     */
    String getFullUrl(String relativePath);
}
