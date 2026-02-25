package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地址实体类
 */
@Data
@TableName("t_address")
public class Address {
    /**
     * 地址ID
     */
    @TableId(value = "address_id", type = IdType.AUTO)
    private Integer addressId;

    /**
     * 地址详情
     */
    private String address;

    /**
     * 区域ID
     */
    private Integer areaId;

    /**
     * 一级区域ID
     */
    @TableField(value = "area_id_1")
    private Integer areaId1;

    /**
     * 二级区域ID
     */
    @TableField(value = "area_id_2")
    private Integer areaId2;

    /**
     * 三级区域ID
     */
    @TableField(value = "area_id_3")
    private Integer areaId3;

    /**
     * 四级区域ID
     */
    @TableField(value = "area_id_4")
    private Integer areaId4;

    /**
     * 区域信息
     */
    private String areaInfo;

    /**
     * 是否默认地址 0-否 1-是
     */
    private Integer isDefault;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 手机号码
     */
    private String mobphone;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 电话号码
     */
    private String telphone;
}
