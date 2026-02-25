package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.AdminMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员菜单Mapper接口
 */
@Mapper
public interface AdminMenuMapper extends BaseMapper<AdminMenu> {
}