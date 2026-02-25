# 管理平台

这是一个基于 Vue 3.0 和 Element Plus 的管理后台系统，用于管理用户和地址信息。

## 技术栈

- Vue 3.0
- Vue Router 4
- Element Plus
- Axios
- Vite

## 功能特性

- 用户管理（增删改查）
- 地址管理（增删改查、设为默认地址）
- 分页查询
- 条件搜索
- 响应式布局

## 项目结构

```
manager/
├── public/
├── src/
│   ├── api/           # API 接口定义
│   ├── assets/        # 静态资源
│   ├── components/    # 组件
│   ├── router/        # 路由配置
│   ├── utils/         # 工具函数
│   ├── views/         # 页面视图
│   ├── App.vue        # 主组件
│   └── main.js        # 入口文件
├── index.html         # HTML 模板
├── package.json       # 项目配置
└── vite.config.js     # Vite 配置
```

## 安装与运行

1. 确保后端服务正在运行（端口 8080）

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

4. 访问 http://localhost:3000

## 构建生产版本

```bash
npm run build
```

## 代理配置

开发环境下，API 请求通过 Vite 代理转发到 http://localhost:8080，避免跨域问题。