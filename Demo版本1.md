# RBAC 权限管理系统 Skill

## 项目概述

这是一个基于 Spring Boot + Vue3 的 RBAC（基于角色的访问控制）权限管理系统，包含用户管理、管理员管理、用户组管理、菜单管理、权限分配等核心功能。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.14
- **JDK**: 1.8
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.3.1
- **安全**: Spring Security + JWT
- **缓存**: Redis
- **验证码**: Kaptcha
- **工具**: Lombok、Hutool

### 前端
- **框架**: Vue 3.4
- **构建工具**: Vite 4.5
- **UI组件库**: Element Plus 2.4
- **路由**: Vue Router 4.2
- **HTTP**: Axios

## 项目结构

```
demo/
├── src/main/java/com/example/demo/
│   ├── common/              # 通用类
│   │   ├── constant/        # 常量
│   │   ├── exception/       # 异常处理
│   │   ├── result/          # 统一响应结果
│   │   └── utils/           # 工具类
│   ├── config/              # 配置类
│   ├── controller/          # 控制器层
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── mapper/              # MyBatis Mapper
│   ├── security/            # 安全相关
│   ├── service/             # 服务层
│   └── util/                # 工具类
├── src/main/resources/
│   ├── static/              # 静态资源
│   ├── application.yml      # 主配置
│   └── application-dev.yml  # 开发环境配置
├── manager/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口封装
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面组件
│   └── vite.config.js       # Vite 配置
└── sql/                     # SQL 脚本
```

## 核心功能模块

### 1. 认证授权模块 (Auth)
- **登录**: `/auth/login` - 支持验证码验证
- **登出**: `/auth/logout`
- **刷新Token**: `/auth/refresh`
- **获取当前用户**: `/auth/me`

### 2. 用户管理模块 (User)
- **实体**: `User` (t_user)
- **接口**:
  - `GET /user/list` - 获取用户列表
  - `GET /user/page` - 分页获取用户
  - `POST /user/save` - 新增用户
  - `PUT /user/update` - 更新用户
  - `DELETE /user/delete/{id}` - 删除用户

### 3. 管理员管理模块 (Admin)
- **实体**: `Admin` (t_admin)
- **字段**: adminId, name, password, groupId, groupName, isSuper, avatar
- **接口**:
  - `GET /admin/list` - 获取管理员列表
  - `GET /admin/page` - 分页获取管理员
  - `GET /admin/group/{groupId}` - 根据用户组获取管理员
  - `POST /admin/create` - 创建管理员（MD5加密密码）
  - `PUT /admin/update` - 更新管理员
  - `DELETE /admin/{id}` - 删除管理员
  - `POST /admin/unlock/{id}` - 解锁管理员账户

### 4. 用户组管理模块 (AdminGroup)
- **实体**: `AdminGroup` (t_admin_group)
- **字段**: groupId, groupName
- **接口**:
  - `GET /admin-group/list` - 获取所有用户组
  - `GET /admin-group/page` - 分页获取用户组
  - `POST /admin-group/create` - 创建用户组
  - `PUT /admin-group/update` - 更新用户组
  - `DELETE /admin-group/{id}` - 删除用户组

### 5. 菜单管理模块 (AdminMenu)
- **实体**: `AdminMenu` (t_admin_menu)
- **字段**: id, name, title, url, parentId, permission, groupId
- **接口**:
  - `GET /admin-menu/list` - 获取所有菜单（扁平结构）
  - `GET /admin-menu/tree` - 获取菜单树形结构
  - `POST /admin-menu/create` - 创建菜单（支持自定义ID）
  - `PUT /admin-menu/update` - 更新菜单
  - `DELETE /admin-menu/{id}` - 删除菜单（级联删除子菜单）

### 6. 权限管理模块 (AdminGroupPermission)
- **实体**: `AdminGroupPermission` (t_admin_group_permission)
- **字段**: id, groupId, menuId
- **接口**:
  - `GET /admin-group-permission/list` - 获取所有权限
  - `GET /admin-group-permission/group/{groupId}` - 获取用户组权限
  - `POST /admin-group-permission/create` - 创建权限
  - `POST /admin-group-permission/batch-create` - 批量创建权限
  - `POST /admin-group-permission/set-permissions` - 设置用户组权限（先删后加）
  - `DELETE /admin-group-permission/{id}` - 删除权限
  - `DELETE /admin-group-permission/group/{groupId}` - 删除用户组所有权限

## 前端路由配置

```javascript
/login              # 登录页
/users              # 用户管理
/addresses          # 地址管理
/admin-groups       # 用户组管理
/admin-menus        # 菜单管理
/admins             # 管理员管理
/admin/page         # 管理员列表（菜单URL映射）
/admin-group/page   # 用户组列表（菜单URL映射）
/admin-menu/list    # 菜单列表（菜单URL映射）
```

## 关键配置

### 后端配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crmeb?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: aA123456

jwt:
  secret: mySecretKeyForJWTTokenGeneration...
  access-token-expiration: 3600000    # 1小时
  refresh-token-expiration: 86400000  # 24小时

redis:
  host: localhost
  port: 6379
```

### 前端代理配置 (vite.config.js)
```javascript
proxy: {
  '/captcha': { target: 'http://localhost:8080' },
  '/auth': { target: 'http://localhost:8080' },
  '/user': { target: 'http://localhost:8080' },
  '/address': { target: 'http://localhost:8080' },
  '/menu': { target: 'http://localhost:8080' },
  '/admin-menu': { target: 'http://localhost:8080' },
  '/admin-group': { target: 'http://localhost:8080' },
  '/admin': { target: 'http://localhost:8080' }
}
```

## 数据库表结构

### 核心表
- **t_user**: 用户表
- **t_admin**: 管理员表
- **t_admin_group**: 用户组表
- **t_admin_menu**: 菜单表
- **t_admin_group_permission**: 用户组权限关联表
- **t_address**: 地址表

## 开发规范

### 后端
- 统一响应格式: `ApiResponse<T>` (code, message, data)
- 密码加密: MD5
- ID生成策略: 菜单支持自定义ID，其他使用自增
- 权限控制: 基于用户组 + 菜单权限

### 前端
- 组件风格: Composition API
- 状态管理: Pinia (authStore)
- 路由守卫: 检查 token 进行权限控制
- 菜单渲染: 支持树形结构，url/path 字段兼容

## 启动方式

### 后端
```bash
mvn spring-boot:run
# 或
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 前端
```bash
cd manager
npm install
npm run dev
```

## 注意事项

1. **菜单URL与路由**: 数据库中菜单的 url 字段必须与前端路由 path 匹配，否则点击菜单会空白
2. **代理配置**: 新增后端接口前缀需要在 vite.config.js 中添加代理规则
3. **ID类型**: 菜单ID支持自定义输入，留空时后端自动生成（当前最大ID+1）
4. **密码加密**: 后端对管理员密码使用 MD5 加密存储
5. **JWT刷新**: 支持 refreshToken 机制，accessToken 过期后可刷新
