-- 创建数据库(如果不存在)
CREATE DATABASE IF NOT EXISTS crmeb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE crmeb;

-- 创建用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(30) DEFAULT NULL COMMENT '姓名',
    age INT(11) DEFAULT NULL COMMENT '年龄',
    email VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO t_user (name, age, email, create_time, update_time) VALUES
('张三', 18, 'zhangsan@example.com', NOW(), NOW()),
('李四', 20, 'lisi@example.com', NOW(), NOW()),
('王五', 25, 'wangwu@example.com', NOW(), NOW());
