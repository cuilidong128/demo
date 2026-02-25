# Spring Security + JWT 认证系统文档

## 系统概述

本系统实现了基于Spring Security和JWT的完整认证解决方案，包括：
- 管理员登录认证
- Access Token + Refresh Token双令牌机制
- Token刷新功能
- 安全登出
- 全局异常处理

## 核心组件

### 1. JWT工具类 (`JwtUtil`)
负责JWT Token的生成、验证和解析：
- `generateAccessToken()`: 生成Access Token（1小时有效期）
- `generateRefreshToken()`: 生成Refresh Token（24小时有效期）
- `validateToken()`: 验证Token有效性
- `extractUsername()`: 从Token中提取用户名

### 2. 认证服务 (`AuthService`)
处理核心认证逻辑：
- `login()`: 用户登录，返回双令牌
- `refreshToken()`: 刷新Access Token
- `logout()`: 用户登出
- `getCurrentAdmin()`: 获取当前认证用户信息

### 3. 安全配置 (`SecurityConfig`)
配置Spring Security策略：
- 无状态会话管理
- CORS配置
- 接口权限控制
- JWT过滤器集成

### 4. 认证控制器 (`AuthController`)
提供RESTful认证接口：
- `POST /api/auth/login`: 管理员登录
- `POST /api/auth/refresh`: 刷新Token
- `POST /api/auth/logout`: 用户登出
- `GET /api/auth/me`: 获取当前用户信息

## 认证流程

### 1. 登录流程
```
客户端 → 发送用户名/密码 → AuthController.login() 
     → AuthService.login() → 认证用户
     → 生成双令牌 → 返回给客户端
```

### 2. 请求认证流程
```
客户端 → 携带Access Token → JwtAuthenticationFilter
     → 验证Token有效性 → 设置安全上下文
     → 放行请求到目标接口
```

### 3. Token刷新流程
```
客户端 → 发送Refresh Token → AuthController.refresh()
     → 验证Refresh Token → 生成新Access Token
     → 返回新令牌给客户端
```

## API接口说明

### 登录接口
```bash
**POST** `/api/auth/login`
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "userInfo": {
      "adminId": 1,
      "name": "admin",
      "avatar": "avatar.jpg",
      "groupId": 1,
      "groupName": "超级管理员组",
      "isSuper": 1
    }
  },
  "timestamp": 1708000000000
}
```

### 刷新Token接口
```bash
**POST** `/api/auth/refresh`
```json
{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 获取用户信息接口
```bash
**GET** `/api/auth/me`
需要在Header中携带: `Authorization: Bearer <access_token>`

### 登出接口
```bash
**POST** `/api/auth/logout`
需要在Header中携带: `Authorization: Bearer <access_token>`

## 安全特性

### 1. 双令牌机制
- **Access Token**: 短期有效（1小时），用于日常API调用
- **Refresh Token**: 长期有效（24小时），用于刷新Access Token

### 2. 权限控制
- 超级管理员拥有 `ROLE_SUPER_ADMIN` 权限
- 普通管理员拥有 `ROLE_ADMIN` 权限
- 支持基于注解的方法级权限控制

### 3. 异常处理
- 统一的全局异常处理器
- 详细的错误信息返回
- 安全的日志记录

## 配置说明

### application.yml 配置
```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughAndSecure123456
  access-token-expiration: 3600000    # 1小时
  refresh-token-expiration: 86400000  # 24小时
```

## 测试验证

### 运行集成测试
```bash
mvn test -Dtest=AuthIntegrationTest
```

### 使用测试脚本
```bash
./test_auth_flow.sh
```

## 使用示例

### 前端调用示例
```javascript
// 登录
const loginResponse = await axios.post('/api/auth/login', {
  username: 'admin',
  password: '123456'
});

const { accessToken, refreshToken } = loginResponse.data.data;

// 存储tokens
localStorage.setItem('access_token', accessToken);
localStorage.setItem('refresh_token', refreshToken);

// 在后续请求中携带token
axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;

// 刷新token
const refreshResponse = await axios.post('/api/auth/refresh', {
  refreshToken: localStorage.getItem('refresh_token')
});
```

## 扩展建议

1. **Token黑名单**: 实现登出后将Token加入黑名单
2. **多设备登录**: 支持同一账号多设备同时登录
3. **验证码**: 登录时增加验证码验证
4. **IP限制**: 限制登录IP范围
5. **登录日志**: 记录详细的登录日志用于审计
6. **密码策略**: 实施强密码策略和定期更换要求

## 注意事项

1. 生产环境中应使用更复杂的密钥
2. 建议启用HTTPS保证传输安全
3. 定期轮换JWT密钥
4. 监控异常登录行为
5. 实施适当的速率限制防止暴力破解
