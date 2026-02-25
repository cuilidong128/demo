package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Album;
import com.example.demo.mapper.AlbumMapper;
import com.example.demo.service.AlbumService;
import org.springframework.stereotype.Service;

/**
 * 相册服务实现类
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {
}
