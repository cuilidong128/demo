#!/bin/bash

# Google Captcha验证码功能测试脚本

echo "=== Google Captcha验证码功能测试 ==="

BASE_URL="http://localhost:8080"

# 测试1: 获取验证码
echo "1. 测试获取验证码..."
CAPTCHA_RESPONSE=$(curl -s -X GET "$BASE_URL/api/captcha/image")

echo "验证码响应:"
echo "$CAPTCHA_RESPONSE" | jq '.'
echo

# 提取UUID用于后续测试
UUID=$(echo "$CAPTCHA_RESPONSE" | jq -r '.data.uuid')
echo "验证码UUID: $UUID"

# 测试2: 验证正确的验证码
echo ""
echo "2. 测试验证正确验证码..."
# 这里需要手动输入从图片中看到的验证码
echo "请查看返回的验证码图片，然后输入看到的验证码:"
read -p "验证码: " CAPTCHA_CODE

if [ -n "$CAPTCHA_CODE" ] && [ -n "$UUID" ]; then
    VALIDATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/captcha/validate" \
      -H "Content-Type: application/json" \
      -d "{\"uuid\":\"$UUID\",\"code\":\"$CAPTCHA_CODE\"}")
    
    echo "验证响应:"
    echo "$VALIDATE_RESPONSE" | jq '.'
    echo
fi

# 测试3: 验证错误的验证码
echo ""
echo "3. 测试验证错误验证码..."
WRONG_VALIDATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/captcha/validate" \
  -H "Content-Type: application/json" \
  -d "{\"uuid\":\"$UUID\",\"code\":\"wrong123\"}")

echo "错误验证码验证响应:"
echo "$WRONG_VALIDATE_RESPONSE" | jq '.'
echo

# 测试4: 测试登录接口（需要有效的验证码）
echo ""
echo "4. 测试带验证码的登录..."

# 先获取新的验证码
NEW_CAPTCHA_RESPONSE=$(curl -s -X GET "$BASE_URL/api/captcha/image")
NEW_UUID=$(echo "$NEW_CAPTCHA_RESPONSE" | jq -r '.data.uuid')
echo "新的验证码UUID: $NEW_UUID"

echo "请查看新的验证码图片，输入验证码进行登录测试:"
read -p "验证码: " LOGIN_CAPTCHA_CODE

if [ -n "$LOGIN_CAPTCHA_CODE" ] && [ -n "$NEW_UUID" ]; then
    LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
      -H "Content-Type: application/json" \
      -d "{
        \"username\":\"admin\",
        \"password\":\"123456\",
        \"captchaUuid\":\"$NEW_UUID\",
        \"captchaCode\":\"$LOGIN_CAPTCHA_CODE\"
      }")
    
    echo "登录响应:"
    echo "$LOGIN_RESPONSE" | jq '.'
    echo
fi

echo "=== 测试完成 ==="
echo
echo "前端测试页面访问地址: http://localhost:8080/captcha-login.html"
echo "请确保Redis服务已启动，并且项目正在运行在8080端口"