# Google Captcha验证码功能文档

## 功能概述

本项目集成了Google Captcha动态验证码图片生成功能，并使用Redis作为本地缓存，实现了用户登录时的图片验证码生成及验证操作。

## 技术架构

- **验证码生成**: Google Kaptcha
- **缓存存储**: Redis
- **图片格式**: Base64编码的PNG图片
- **验证机制**: UUID + 验证码文本双重验证

## 核心组件

### 1. 依赖配置 (pom.xml)
```xml
<!-- Redis Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Google Captcha -->
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>
```

### 2. 配置文件 (application.yml)
```yaml
# Redis配置
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
    timeout: 5000ms

# 验证码配置
captcha:
  expire-time: 300  # 验证码过期时间（秒）
  width: 120        # 验证码图片宽度
  height: 40        # 验证码图片高度
  length: 4         # 验证码字符长度
```

### 3. 核心类说明

#### CaptchaConfig.java
- 配置Google Kaptcha参数
- 设置验证码样式、颜色、尺寸等

#### CaptchaService.java & CaptchaServiceImpl.java
- 验证码生成、验证、删除核心逻辑
- Redis缓存操作
- Base64图片编码

#### CaptchaController.java
- 提供验证码相关API接口
- `/api/captcha/image` - 获取验证码图片
- `/api/captcha/validate` - 验证验证码

#### 修改的类
- `LoginRequest.java` - 添加验证码字段
- `AuthServiceImpl.java` - 集成验证码验证逻辑

## API接口说明

### 1. 获取验证码图片
```
GET /api/captcha/image
```

**响应示例:**
```json
{
  "code": 200,
  "message": "验证码生成成功",
  "data": {
    "uuid": "550e8400e29b41d4a716446655440000",
    "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHgAAAA..."
  }
}
```

### 2. 验证验证码
```
POST /api/captcha/validate
```

**请求参数:**
```json
{
  "uuid": "550e8400e29b41d4a716446655440000",
  "code": "abcd"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "验证码正确",
  "data": true
}
```

### 3. 用户登录（集成验证码）
```
POST /api/auth/login
```

**请求参数:**
```json
{
  "username": "admin",
  "password": "123456",
  "captchaUuid": "550e8400e29b41d4a716446655440000",
  "captchaCode": "abcd"
}
```

## 安全特性

1. **一次性验证**: 验证码验证成功后立即从Redis中删除
2. **时效性**: 默认5分钟过期时间
3. **唯一标识**: 使用UUID确保每个验证码的唯一性
4. **大小写不敏感**: 验证时忽略大小写差异
5. **防爆破**: 错误验证后自动刷新验证码

## 前端集成示例

### HTML表单
```html
<form id="loginForm">
    <input type="text" name="username" placeholder="用户名">
    <input type="password" name="password" placeholder="密码">
    
    <div class="captcha-container">
        <img id="captchaImage" src="" alt="验证码">
        <button type="button" onclick="getCaptcha()">刷新</button>
        <input type="hidden" id="captchaUuid" name="captchaUuid">
    </div>
    <input type="text" name="captchaCode" placeholder="验证码">
    
    <button type="submit">登录</button>
</form>
```

### JavaScript逻辑
```javascript
// 获取验证码
function getCaptcha() {
    fetch('/api/captcha/image')
        .then(response => response.json())
        .then(data => {
            document.getElementById('captchaImage').src = data.data.image;
            document.getElementById('captchaUuid').value = data.data.uuid;
        });
}

// 表单提交
document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    // 收集表单数据并提交
});
```

## 测试验证

### 1. 启动服务
确保Redis服务运行，然后启动Spring Boot应用

### 2. 使用测试脚本
```bash
./test_captcha.sh
```

### 3. 访问测试页面
浏览器访问: `http://localhost:8080/captcha-login.html`

## 部署注意事项

1. **Redis配置**: 生产环境需要配置Redis连接信息和密码
2. **安全性**: 建议缩短验证码过期时间（如2-3分钟）
3. **监控**: 建议监控Redis内存使用情况
4. **备份**: 定期备份Redis数据

## 扩展功能

可考虑的扩展功能：
- 图形验证码难度调节
- 滑动验证码支持
- 短信验证码集成
- 多语言验证码支持
- 验证码统计分析

## 常见问题

**Q: 验证码图片无法显示？**
A: 检查Redis连接是否正常，查看控制台错误日志

**Q: 验证码总是验证失败？**
A: 确认验证码是否过期，检查大小写是否正确

**Q: 如何调整验证码复杂度？**
A: 修改CaptchaConfig.java中的相关参数配置