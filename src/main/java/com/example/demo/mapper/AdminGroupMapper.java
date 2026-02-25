package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.AdminGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员组Mapper接口
 */
@Mapper
public interface AdminGroupMapper extends BaseMapper<AdminGroup> {
}