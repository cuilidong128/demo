package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Album;
import org.apache.ibatis.annotations.Mapper;

/**
 * 相册Mapper接口
 */
@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
}
