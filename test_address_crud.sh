#!/bin/bash

# Address CRUD 测试脚本

echo "=== Address CRUD 功能测试 ==="

# 测试保存地址
echo "1. 测试保存地址:"
curl -X POST http://localhost:8080/address/save \
  -H "Content-Type: application/json" \
  -d '{
    "address": "北京市朝阳区测试街道123号",
    "areaId": 110105,
    "areaId1": 110000,
    "areaId2": 110100,
    "areaId3": 110105,
    "areaInfo": "北京市/朝阳区",
    "isDefault": 1,
    "memberId": 1001
  }'
echo -e "\n"

# 测试查询所有地址
echo "2. 测试查询所有地址:"
curl -X GET http://localhost:8080/address/list
echo -e "\n"

# 测试分页查询
echo "3. 测试分页查询:"
curl -X GET "http://localhost:8080/address/page?current=1&size=10"
echo -e "\n"

# 测试条件查询
echo "4. 测试条件查询(按会员ID):"
curl -X GET "http://localhost:8080/address/listByCondition?memberId=1001"
echo -e "\n"

# 测试查询默认地址
echo "5. 测试查询用户默认地址:"
curl -X GET http://localhost:8080/address/default/1001
echo -e "\n"

# 测试更新地址
echo "6. 测试更新地址:"
curl -X PUT http://localhost:8080/address/update \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "address": "北京市海淀区更新街道456号",
    "areaInfo": "北京市/海淀区",
    "isDefault": 1,
    "memberId": 1001
  }'
echo -e "\n"

# 测试设置默认地址
echo "7. 测试设置默认地址:"
curl -X PUT http://localhost:8080/address/setDefault/1/1001
echo -e "\n"

# 测试根据ID查询地址
echo "8. 测试根据ID查询地址:"
curl -X GET http://localhost:8080/address/get/1
echo -e "\n"

# 测试删除地址
echo "9. 测试删除地址:"
curl -X DELETE http://localhost:8080/address/delete/1
echo -e "\n"

echo "=== 测试完成 ==="