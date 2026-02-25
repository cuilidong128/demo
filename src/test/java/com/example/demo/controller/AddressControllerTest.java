package com.example.demo.controller;

import com.example.demo.entity.Address;
import com.example.demo.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AddressControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AddressService addressService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddressCRUD() throws Exception {
        // 1. 测试保存地址
        Address address = new Address();
        address.setAddress("北京市朝阳区测试街道123号");
        address.setAreaId(110105);
        address.setAreaId1(110000);
        address.setAreaId2(110100);
        address.setAreaId3(110105);
        address.setAreaInfo("北京市/朝阳区");
        address.setIsDefault(1);
        address.setMemberId(1001);


        boolean saveResult = addressService.save(address);
        assertTrue(saveResult, "地址保存应该成功");
        assertNotNull(address.getAddressId(), "地址ID应该不为空");

        Integer savedAddressId = address.getAddressId();

        // 2. 测试根据ID查询地址
        Address foundAddress = addressService.getById(savedAddressId);
        assertNotNull(foundAddress, "应该能根据ID找到地址");
        assertEquals("北京市朝阳区测试街道123号", foundAddress.getAddress());
        assertEquals(Integer.valueOf(1001), foundAddress.getMemberId());

        // 3. 测试查询所有地址
        List<Address> allAddresses = addressService.list();
        assertFalse(allAddresses.isEmpty(), "地址列表不应该为空");

        // 4. 测试根据会员ID查询地址
        List<Address> memberAddresses = addressService.listByMemberId(1001);
        assertFalse(memberAddresses.isEmpty(), "会员地址列表不应该为空");

        // 5. 测试更新地址
        Address updateAddress = new Address();
        updateAddress.setAddressId(savedAddressId);
        updateAddress.setAddress("北京市海淀区更新街道456号");
        updateAddress.setAreaInfo("北京市/海淀区");


        boolean updateResult = addressService.updateById(updateAddress);
        assertTrue(updateResult, "地址更新应该成功");

        // 验证更新结果
        Address updatedAddress = addressService.getById(savedAddressId);
        assertEquals("北京市海淀区更新街道456号", updatedAddress.getAddress());

        // 6. 测试设置默认地址
        boolean setDefaultResult = addressService.setDefaultAddress(savedAddressId, 1001);
        assertTrue(setDefaultResult, "设置默认地址应该成功");

        // 验证默认地址设置
        Address defaultAddress = addressService.getDefaultAddress(1001);
        assertNotNull(defaultAddress, "应该能获取到默认地址");
        assertEquals(savedAddressId, defaultAddress.getAddressId());

        // 7. 测试根据区域信息查询
        List<Address> areaAddresses = addressService.listByAreaInfo("北京市");
        assertFalse(areaAddresses.isEmpty(), "根据区域信息查询应该返回结果");

        // 8. 测试删除地址
        boolean deleteResult = addressService.removeById(savedAddressId);
        assertTrue(deleteResult, "地址删除应该成功");

        // 验证删除结果
        Address deletedAddress = addressService.getById(savedAddressId);
        assertNull(deletedAddress, "删除后的地址应该为null");
    }

    @Test
    public void testAddressControllerEndpoints() throws Exception {
        // 测试保存地址接口
        String addressJson = "{\n" +
                "  \"address\": \"上海市浦东新区测试路789号\",\n" +
                "  \"areaId\": 310115,\n" +
                "  \"areaId1\": 310000,\n" +
                "  \"areaId2\": 310100,\n" +
                "  \"areaId3\": 310115,\n" +
                "  \"areaInfo\": \"上海市/浦东新区\",\n" +
                "  \"isDefault\": 0,\n" +
                "  \"memberId\": 1002\n" +
                "}";

        mockMvc.perform(post("/address/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addressJson))
                .andExpect(status().isOk())
                .andExpect(content().string("保存成功"));

        // 测试查询所有地址接口
        mockMvc.perform(get("/address/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 测试分页查询接口
        mockMvc.perform(get("/address/page")
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray());

        // 测试条件查询接口
        mockMvc.perform(get("/address/listByCondition")
                .param("memberId", "1002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testAddressBusinessLogic() {
        // 测试业务逻辑方法
        Address address1 = new Address();
        address1.setAddress("测试地址1");
        address1.setMemberId(2001);
        address1.setIsDefault(0);


        Address address2 = new Address();
        address2.setAddress("测试地址2");
        address2.setMemberId(2001);
        address2.setIsDefault(0);


        // 保存测试数据
        addressService.save(address1);
        addressService.save(address2);

        // 测试设置默认地址功能
        boolean result1 = addressService.setDefaultAddress(address1.getAddressId(), 2001);
        assertTrue(result1, "设置默认地址应该成功");

        Address defaultAddr = addressService.getDefaultAddress(2001);
        assertNotNull(defaultAddr, "应该能获取默认地址");
        assertEquals(address1.getAddressId(), defaultAddr.getAddressId());

        // 清理测试数据
        addressService.removeById(address1.getAddressId());
        addressService.removeById(address2.getAddressId());
    }
}
