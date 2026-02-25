#!/bin/bash

# 明文密码登录测试脚本

echo "=== 明文密码登录测试 ==="

BASE_URL="http://localhost:8080"

# 测试登录接口
echo "测试明文密码登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "captchaUuid": "test-uuid",
    "captchaCode": "test"
  }')

echo "登录响应:"
echo "$LOGIN_RESPONSE" | jq '.'
echo

# 检查登录结果
if echo "$LOGIN_RESPONSE" | grep -q '"code":200'; then
    echo "✅ 明文密码登录成功"
    ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
    echo "Access Token: ${ACCESS_TOKEN:0:50}..."
else
    echo "❌ 明文密码登录失败"
fi

echo ""
echo "=== 测试完成 ==="