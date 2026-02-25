package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.AlbumFiles;
import com.example.demo.mapper.AlbumFilesMapper;
import com.example.demo.service.AlbumFilesService;
import org.springframework.stereotype.Service;

/**
 * 相册文件服务实现类
 */
@Service
public class AlbumFilesServiceImpl extends ServiceImpl<AlbumFilesMapper, AlbumFiles> implements AlbumFilesService {
}
