package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Address;

import java.util.List;

/**
 * 地址Service接口扩展
 */
public interface AddressService extends IService<Address> {
    
    /**
     * 根据会员ID查询地址列表
     * @param memberId 会员ID
     * @return 地址列表
     */
    List<Address> listByMemberId(Integer memberId);
    
    /**
     * 获取会员默认地址
     * @param memberId 会员ID
     * @return 默认地址
     */
    Address getDefaultAddress(Integer memberId);
    
    /**
     * 设置默认地址
     * @param addressId 地址ID
     * @param memberId 会员ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Integer addressId, Integer memberId);
    
    /**
     * 根据区域信息查询地址
     * @param areaInfo 区域信息
     * @return 地址列表
     */
    List<Address> listByAreaInfo(String areaInfo);
}