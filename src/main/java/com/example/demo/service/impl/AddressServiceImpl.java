package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Address;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.service.AddressService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址Service实现类扩展
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> listByMemberId(Integer memberId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        return this.list(queryWrapper);
    }

    @Override
    public Address getDefaultAddress(Integer memberId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("is_default", 1);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean setDefaultAddress(Integer addressId, Integer memberId) {
        try {
            // 先将该用户的所有地址设置为非默认
            QueryWrapper<Address> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("member_id", memberId);
            Address updateAddress = new Address();
            updateAddress.setIsDefault(0);

            this.update(updateAddress, updateWrapper);

            // 再将指定地址设置为默认
            Address defaultAddress = new Address();
            defaultAddress.setAddressId(addressId);
            defaultAddress.setIsDefault(1);

            return this.updateById(defaultAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Address> listByAreaInfo(String areaInfo) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("area_info", areaInfo);
        return this.list(queryWrapper);
    }
}
