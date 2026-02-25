# Address CRUD 功能测试报告

## 项目概述
为 `crmeb.address` 表生成完整的 CRUD 操作文件，包括实体类、Mapper、Service、Controller 以及相应的测试。

## 生成的文件清单

### 1. 实体类 (Entity)
**文件路径**: `src/main/java/com/example/demo/entity/Address.java`

**主要字段**:
- `addressId`: 地址ID (主键，自增)
- `address`: 地址详情
- `areaId`: 区域ID
- `areaId1`-`areaId4`: 多级区域ID
- `areaInfo`: 区域信息
- `isDefault`: 是否默认地址 (0-否, 1-是)
- `memberId`: 会员ID
- `createTime`: 创建时间
- `updateTime`: 更新时间

### 2. Mapper 接口
**文件路径**: `src/main/java/com/example/demo/mapper/AddressMapper.java`

**功能**: 继承 MyBatis-Plus 的 BaseMapper，提供基础的 CRUD 操作

### 3. Service 层
**接口文件**: `src/main/java/com/example/demo/service/AddressService.java`
**实现类**: `src/main/java/com/example/demo/service/impl/AddressServiceImpl.java`

**扩展功能**:
- `listByMemberId(Integer memberId)`: 根据会员ID查询地址
- `getDefaultAddress(Integer memberId)`: 获取会员默认地址
- `setDefaultAddress(Integer addressId, Integer memberId)`: 设置默认地址
- `listByAreaInfo(String areaInfo)`: 根据区域信息查询地址

### 4. Controller 控制器
**文件路径**: `src/main/java/com/example/demo/controller/AddressController.java`

**API 接口**:
- `POST /address/save`: 新增地址
- `DELETE /address/delete/{id}`: 删除地址
- `PUT /address/update`: 更新地址
- `GET /address/get/{id}`: 根据ID查询地址
- `GET /address/list`: 查询所有地址
- `GET /address/page`: 分页查询地址
- `GET /address/listByCondition`: 条件查询地址
- `GET /address/default/{memberId}`: 查询用户默认地址
- `PUT /address/setDefault/{addressId}/{memberId}`: 设置默认地址

### 5. 测试类
**文件路径**: `src/test/java/com/example/demo/controller/AddressControllerTest.java`

**测试内容**:
- 完整的 CRUD 操作测试
- 业务逻辑测试
- Controller 接口测试
- 数据验证测试

## 测试方法

### 1. 自动化测试 (需要 Maven)
```bash
cd /Users/cuilidong/Desktop/demo
mvn test -Dtest=AddressControllerTest
```

### 2. 手动 API 测试
使用提供的测试脚本:
```bash
./test_address_crud.sh
```

### 3. 单个接口测试示例

**保存地址**:
```bash
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
```

**查询所有地址**:
```bash
curl -X GET http://localhost:8080/address/list
```

**分页查询**:
```bash
curl -X GET "http://localhost:8080/address/page?current=1&size=10"
```

## 功能特点

### 1. 完整的 CRUD 操作
- 支持地址的增删改查
- 自动处理创建时间和更新时间
- 数据验证和错误处理

### 2. 业务逻辑处理
- 默认地址管理
- 按会员查询地址
- 按区域信息查询
- 分页查询支持

### 3. 数据一致性保证
- 设置默认地址时自动取消其他地址的默认状态
- 完整的事务处理
- 异常回滚机制

### 4. 灵活的查询条件
- 支持多条件组合查询
- 模糊查询支持
- 分页查询优化

## 部署和运行

### 1. 启动应用
```bash
cd /Users/cuilidong/Desktop/demo
./start.sh
```

### 2. 验证服务
访问: http://localhost:8080/address/list

### 3. 运行测试
确保数据库连接正常后，执行测试脚本或单元测试。

## 注意事项

1. **数据库配置**: 确保 `application.yml` 中的数据库连接配置正确
2. **表结构**: 确保 `crmeb.address` 表已存在且结构正确
3. **依赖项**: 确保所有 Maven 依赖已正确安装
4. **端口冲突**: 确保 8080 端口未被其他应用占用

## 预期结果

所有 CRUD 操作应该能够正常执行，返回正确的响应结果，数据在数据库中正确存储和更新。