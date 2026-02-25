package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.result.Result;
import com.example.demo.entity.Address;
import com.example.demo.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址Controller
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 新增地址
     */
    @PostMapping("/save")
    public String save(@RequestBody Address address) {

        boolean result = addressService.save(address);
        return result ? "保存成功" : "保存失败";
    }

    /**
     * 根据ID删除地址
     */
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        boolean result = addressService.removeById(id);
        return result ? "删除成功" : "删除失败";
    }

    /**
     * 根据ID更新地址
     */
    @PutMapping("/update")
    public String update(@RequestBody Address address) {

        boolean result = addressService.updateById(address);
        return result ? "更新成功" : "更新失败";
    }

    /**
     * 根据ID查询地址
     */
    @GetMapping("/get/{id}")
    public Address getAddressById(@PathVariable Integer id) {
        return addressService.getById(id);
    }

    /**
     * 查询所有地址
     */
    @GetMapping("/list")
    public Result<List<Address>> listAll() {
        return new Result<>(addressService.list());
    }

    /**
     * 分页查询地址
     */
    @GetMapping("/page")
    public Result<Page<Address>> page(@RequestParam(defaultValue = "1") Integer current,
                             @RequestParam(defaultValue = "10") Integer size) {
        Page<Address> page = new Page<>(current, size);
        return new Result<>(addressService.page(page));
    }

    /**
     * 根据条件查询地址
     */
    @GetMapping("/listByCondition")
    public List<Address> listByCondition(@RequestParam(required = false) String address,
                                        @RequestParam(required = false) Integer memberId,
                                        @RequestParam(required = false) Integer isDefault) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        if (address != null && !address.isEmpty()) {
            queryWrapper.like("address", address);
        }
        if (memberId != null) {
            queryWrapper.eq("member_id", memberId);
        }
        if (isDefault != null) {
            queryWrapper.eq("is_default", isDefault);
        }
        return addressService.list(queryWrapper);
    }

    /**
     * 查询用户默认地址
     */
    @GetMapping("/default/{memberId}")
    public Address getDefaultAddress(@PathVariable Integer memberId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("is_default", 1);
        return addressService.getOne(queryWrapper);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/setDefault/{addressId}/{memberId}")
    public String setDefaultAddress(@PathVariable Integer addressId, @PathVariable Integer memberId) {
        try {
            // 先将该用户的所有地址设置为非默认
            QueryWrapper<Address> updateWrapper = new QueryWrapper<>();
            updateWrapper.eq("member_id", memberId);
            Address updateAddress = new Address();
            updateAddress.setIsDefault(0);

            addressService.update(updateAddress, updateWrapper);

            // 再将指定地址设置为默认
            Address defaultAddress = new Address();
            defaultAddress.setAddressId(addressId);
            defaultAddress.setIsDefault(1);

            boolean result = addressService.updateById(defaultAddress);

            return result ? "设置默认地址成功" : "设置默认地址失败";
        } catch (Exception e) {
            return "设置默认地址失败: " + e.getMessage();
        }
    }
}
