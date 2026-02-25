package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.AdminGroupPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员组权限Mapper接口
 */
@Mapper
public interface AdminGroupPermissionMapper extends BaseMapper<AdminGroupPermission> {
}