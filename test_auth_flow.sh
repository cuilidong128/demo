#!/bin/bash

# JWT认证测试脚本

echo "=== JWT认证系统测试 ==="
echo

# 服务器地址
BASE_URL="http://localhost:8080"

# 测试用户凭据
USERNAME="admin"
PASSWORD="123456"

echo "1. 测试登录接口..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

echo "登录响应:"
echo "$LOGIN_RESPONSE" | jq '.'
echo

# 提取access token和refresh token
ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.refreshToken')

if [ "$ACCESS_TOKEN" != "null" ]; then
    echo "✅ 登录成功"
    echo "Access Token: $ACCESS_TOKEN"
    echo "Refresh Token: $REFRESH_TOKEN"
    echo
    
    echo "2. 测试访问受保护的接口..."
    PROTECTED_RESPONSE=$(curl -s -X GET "$BASE_URL/api/admin/list" \
      -H "Authorization: Bearer $ACCESS_TOKEN")
    
    echo "受保护接口响应:"
    echo "$PROTECTED_RESPONSE" | jq '.'
    echo
    
    echo "3. 测试刷新Token..."
    REFRESH_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/refresh" \
      -H "Content-Type: application/json" \
      -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")
    
    echo "刷新Token响应:"
    echo "$REFRESH_RESPONSE" | jq '.'
    echo
    
    NEW_ACCESS_TOKEN=$(echo "$REFRESH_RESPONSE" | jq -r '.data.accessToken')
    
    echo "4. 测试获取当前用户信息..."
    ME_RESPONSE=$(curl -s -X GET "$BASE_URL/api/auth/me" \
      -H "Authorization: Bearer $NEW_ACCESS_TOKEN")
    
    echo "用户信息响应:"
    echo "$ME_RESPONSE" | jq '.'
    echo
    
    echo "5. 测试登出..."
    LOGOUT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/logout" \
      -H "Authorization: Bearer $NEW_ACCESS_TOKEN")
    
    echo "登出响应:"
    echo "$LOGOUT_RESPONSE" | jq '.'
    echo
    
    echo "✅ 认证流程测试完成"
else
    echo "❌ 登录失败"
fi

echo
echo "=== 测试结束 ==="