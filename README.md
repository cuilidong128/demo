# Spring Boot + MyBatis-Plus CRUD 示例

这是一个基于Spring Boot和MyBatis-Plus实现的简单CRUD示例项目。

## 项目结构

```
src/main/java/com/example/demo/
├── DemoApplication.java          # Spring Boot启动类
├── controller/
│   └── UserController.java       # 用户Controller，实现CRUD接口
├── entity/
│   └── User.java                 # 用户实体类
├── mapper/
│   └── UserMapper.java           # 用户Mapper接口
└── service/
    ├── UserService.java          # 用户Service接口
    └── impl/
        └── UserServiceImpl.java  # 用户Service实现类

src/main/resources/
├── application.properties        # 配置文件
└── sql/
    └── user_table.sql           # 数据库表结构SQL
```

## 技术栈

- Spring Boot 2.7.14
- MyBatis-Plus 3.5.3.1
- MySQL 8.0+
- Lombok
- Java 8

## 数据库配置

在 `application.properties` 中配置MySQL连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=123456
```

## 数据库表结构

执行 `sql/user_table.sql` 文件创建数据库和表：

```sql
CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE test;

CREATE TABLE user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(30) DEFAULT NULL COMMENT '姓名',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

## API接口说明

### 1. 新增用户
- **URL**: `POST /user/save`
- **参数**: JSON格式的User对象
- **示例**:
```json
{
  "name": "张三",
  "age": 25,
  "email": "zhangsan@example.com"
}
```

### 2. 删除用户
- **URL**: `DELETE /user/delete/{id}`
- **参数**: 路径变量id

### 3. 更新用户
- **URL**: `PUT /user/update`
- **参数**: JSON格式的User对象(需要包含id)

### 4. 查询用户(根据ID)
- **URL**: `GET /user/get/{id}`
- **参数**: 路径变量id

### 5. 查询所有用户
- **URL**: `GET /user/list`

### 6. 分页查询用户
- **URL**: `GET /user/page`
- **参数**: 
  - current: 当前页码(默认1)
  - size: 每页大小(默认10)

### 7. 条件查询用户
- **URL**: `GET /user/listByCondition`
- **参数**: 
  - name: 姓名(模糊查询)
  - age: 年龄(精确查询)

## 运行项目

1. 确保已安装Java 8+和Maven
2. 创建MySQL数据库并执行SQL脚本
3. 修改application.properties中的数据库连接配置
4. 运行命令：
```bash
mvn spring-boot:run
```
或
```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 测试示例

项目启动后，可以通过以下方式测试：

### 使用curl测试

```bash
# 新增用户
curl -X POST http://localhost:8080/user/save \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户","age":30,"email":"test@example.com"}'

# 查询所有用户
curl http://localhost:8080/user/list

# 根据ID查询用户
curl http://localhost:8080/user/get/1

# 分页查询
curl "http://localhost:8080/user/page?current=1&size=5"

# 条件查询
curl "http://localhost:8080/user/listByCondition?name=张&age=25"

# 更新用户
curl -X PUT http://localhost:8080/user/update \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"更新后的姓名","age":26,"email":"updated@example.com"}'

# 删除用户
curl -X DELETE http://localhost:8080/user/delete/1
```

## MyBatis-Plus 特性

本项目使用MyBatis-Plus提供的便捷功能：

1. **BaseMapper**: 提供基础CRUD操作
2. **IService**: 提供Service层通用方法
3. **分页插件**: 自动分页功能
4. **条件构造器**: QueryWrapper实现复杂查询
5. **自动填充**: createTime和updateTime自动设置

## 注意事项

1. 确保MySQL服务已启动
2. 数据库连接信息需要根据实际情况修改
3. Lombok需要IDE插件支持才能正常显示getter/setter方法
4. MyBatis-Plus会自动处理常见的数据库操作，减少重复代码