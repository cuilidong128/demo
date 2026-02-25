#!/bin/bash

# Admin模块CRUD功能测试脚本
echo "==================================="
echo "开始测试Admin模块CRUD功能"
echo "==================================="

BASE_URL="http://localhost:8080/api/admin"

# 测试1: 创建管理员
echo "测试1: 创建管理员..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/create" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test_admin_'$(date +%s)'",
    "password": "test_password_123",
    "isSuper": 0,
    "groupName": "测试管理员组"
  }')

echo "创建响应: $CREATE_RESPONSE"

# 检查是否创建成功
if echo "$CREATE_RESPONSE" | grep -q '"code":200'; then
    echo "✅ 管理员创建成功"
    
    # 提取创建的管理员ID
    ADMIN_ID=$(echo "$CREATE_RESPONSE" | grep -o '"adminId":[0-9]*' | grep -o '[0-9]*')
    echo "创建的管理员ID: $ADMIN_ID"
else
    echo "❌ 管理员创建失败"
fi

# 测试2: 获取所有管理员
echo ""
echo "测试2: 获取所有管理员..."
LIST_RESPONSE=$(curl -s "$BASE_URL/list")
echo "列表响应: $LIST_RESPONSE"

if echo "$LIST_RESPONSE" | grep -q '"code":200'; then
    echo "✅ 获取管理员列表成功"
else
    echo "❌ 获取管理员列表失败"
fi

# 测试3: 分页获取管理员
echo ""
echo "测试3: 分页获取管理员..."
PAGE_RESPONSE=$(curl -s "$BASE_URL/page?pageNum=1&pageSize=5")
echo "分页响应: $PAGE_RESPONSE"

if echo "$PAGE_RESPONSE" | grep -q '"code":200'; then
    echo "✅ 分页获取成功"
else
    echo "❌ 分页获取失败"
fi

# 测试4: 根据用户名查询管理员
echo ""
echo "测试4: 根据用户名查询管理员..."
NAME_RESPONSE=$(curl -s "$BASE_URL/name/test_admin_$(date +%s)")
echo "按用户名查询响应: $NAME_RESPONSE"

echo ""
echo "==================================="
echo "Admin模块基础测试完成"
echo "==================================="