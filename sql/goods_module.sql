-- ============================================
-- 商品管理模块数据库表结构
-- ============================================

-- 1. 商品类目表
CREATE TABLE IF NOT EXISTS t_category
(
    category_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '类目ID',
    app_image     VARCHAR(255) NULL COMMENT '移动端图片',
    category_name VARCHAR(255) NOT NULL COMMENT '类目名称',
    category_sort INT          NULL DEFAULT 0 COMMENT '排序',
    deep          INT          NULL DEFAULT 1 COMMENT '层级深度(1-3)',
    parent_id     INT          NULL DEFAULT 0 COMMENT '父级ID,0为顶级',
    create_time   DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品类目表';

-- 2. 商品属性表
CREATE TABLE IF NOT EXISTS t_attribute
(
    attribute_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '属性ID',
    attribute_name VARCHAR(255) NULL COMMENT '属性名称',
    attribute_sort INT          NULL DEFAULT 0 COMMENT '排序',
    category_id    INT          NULL COMMENT '所属类目ID',
    is_show        INT          NULL DEFAULT 1 COMMENT '是否显示:0否,1是',
    create_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品属性表';

-- 3. 商品属性值表
CREATE TABLE IF NOT EXISTS t_attribute_value
(
    attribute_value_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '属性值ID',
    attribute_id         INT          NULL COMMENT '属性ID',
    attribute_value_name VARCHAR(255) NULL COMMENT '属性值名称',
    create_time          DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品属性值表';

-- 4. 商品SPU表 (goods_common)
CREATE TABLE IF NOT EXISTS t_goods_common
(
    common_id               INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品SPU ID',
    goods_name              VARCHAR(255)   NULL COMMENT '商品名称',
    jingle                  VARCHAR(500)   NULL COMMENT '商品卖点',
    category_id             VARCHAR(255)   NULL COMMENT '类目ID列表，多个用逗号分隔',
    category_id_1           INT            NULL COMMENT '一级类目ID',
    category_id_2           INT            NULL COMMENT '二级类目ID',
    category_id_3           INT            NULL COMMENT '三级类目ID',
    brand_id                INT            NULL COMMENT '品牌ID',
    store_id                INT            NULL DEFAULT 0 COMMENT '店铺ID',
    goods_state             INT            NULL DEFAULT 0 COMMENT '商品状态:0下架,1正常,10违规禁售',
    goods_verify            INT            NULL DEFAULT 0 COMMENT '审核状态:0未通过,1已通过,10审核中',
    goods_image             VARCHAR(255)   NULL COMMENT '主图',
    goods_images            TEXT           NULL COMMENT '轮播图列表，JSON格式',
    goods_price             DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '商品价格',
    goods_market_price      DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '市场价',
    goods_cost_price        DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '成本价',
    goods_storage           INT            NULL DEFAULT 0 COMMENT '商品库存',
    goods_sale_num          INT            NULL DEFAULT 0 COMMENT '销售数量',
    goods_click             INT            NULL DEFAULT 0 COMMENT '点击数量',
    goods_favorite          INT            NULL DEFAULT 0 COMMENT '收藏数量',
    evaluate_num            INT            NULL DEFAULT 0 COMMENT '评价数量',
    goods_rate              INT            NULL DEFAULT 100 COMMENT '好评率',
    freight_template_id     INT            NULL COMMENT '运费模板ID',
    freight_weight          DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '商品重量(kg)',
    freight_volume          DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '商品体积(m³)',
    unit_name               VARCHAR(50)    NULL COMMENT '计量单位',
    is_commend              INT            NULL DEFAULT 0 COMMENT '是否推荐:0否,1是',
    is_distribution         INT            NULL DEFAULT 0 COMMENT '是否分销:0否,1是',
    is_points_goods         INT            NULL DEFAULT 0 COMMENT '是否积分商品:0否,1是',
    is_gift                 INT            NULL DEFAULT 0 COMMENT '是否有赠品:0否,1是',
    commission_rate         INT            NULL DEFAULT 0 COMMENT '分销佣金比例%',
    search_boost            INT            NULL DEFAULT 0 COMMENT '搜索优先级',
    spec_json               TEXT           NULL COMMENT '规格JSON',
    goods_spec_names        VARCHAR(500)   NULL COMMENT '规格名称JSON',
    create_time             DATETIME       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time             DATETIME       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_goods_state (goods_state),
    INDEX idx_goods_verify (goods_verify)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品SPU表';

-- 5. 商品SKU表 (goods)
CREATE TABLE IF NOT EXISTS t_goods
(
    goods_id         INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品SKU ID',
    common_id        INT            NULL COMMENT 'SPU ID',
    goods_name       VARCHAR(255)   NULL COMMENT 'SKU名称',
    goods_specs      VARCHAR(500)   NULL COMMENT '规格值组合',
    goods_full_specs VARCHAR(500)   NULL COMMENT '完整规格信息',
    goods_price      DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '销售价格',
    goods_market_price DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '市场价',
    goods_cost_price DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '成本价',
    goods_storage    INT            NULL DEFAULT 0 COMMENT '库存',
    goods_storage_alarm INT         NULL DEFAULT 0 COMMENT '库存预警值',
    goods_serial     VARCHAR(100)   NULL COMMENT '商品货号',
    goods_barcode    VARCHAR(100)   NULL COMMENT '商品条形码',
    image_name       VARCHAR(255)   NULL COMMENT 'SKU图片',
    color_id         INT            NULL COMMENT '颜色ID',
    is_default       INT            NULL DEFAULT 0 COMMENT '是否默认:0否,1是',
    create_time      DATETIME       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_common_id (common_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品SKU表';

-- 6. 商品详情表
CREATE TABLE IF NOT EXISTS t_goods_common_body
(
    common_id     INT PRIMARY KEY COMMENT '商品SPU ID',
    goods_body    LONGTEXT NULL COMMENT 'PC端详情',
    mobile_body   LONGTEXT NULL COMMENT '移动端详情',
    format_top    INT      NULL DEFAULT 0 COMMENT '顶部格式',
    format_bottom INT      NULL DEFAULT 0 COMMENT '底部格式'
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品详情表';

-- 7. 商品图片表
CREATE TABLE IF NOT EXISTS t_goods_image
(
    image_id   INT AUTO_INCREMENT PRIMARY KEY COMMENT '图片ID',
    common_id  INT          NULL COMMENT 'SPU ID',
    color_id   INT          NULL COMMENT '颜色ID',
    image_name VARCHAR(255) NULL COMMENT '图片路径',
    image_sort INT          NULL DEFAULT 0 COMMENT '排序',
    is_default INT          NULL DEFAULT 0 COMMENT '是否默认:0否,1是',
    create_time DATETIME    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_common_id (common_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品图片表';

-- 8. 商品与属性值关联表
CREATE TABLE IF NOT EXISTS t_goods_attribute
(
    common_id          INT NOT NULL COMMENT 'SPU ID',
    attribute_value_id INT NOT NULL COMMENT '属性值ID',
    PRIMARY KEY (common_id, attribute_value_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='商品属性关联表';

-- 9. 相册表
CREATE TABLE IF NOT EXISTS t_album
(
    album_id    INT AUTO_INCREMENT PRIMARY KEY COMMENT '相册ID',
    album_name  VARCHAR(255) NOT NULL COMMENT '相册名称',
    parent_id   INT          NULL DEFAULT 0 COMMENT '父级ID,0为顶级',
    store_id    INT          NULL DEFAULT 0 COMMENT '店铺ID',
    create_time DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='相册表';

-- 10. 相册文件表
CREATE TABLE IF NOT EXISTS t_album_files
(
    files_id      INT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    album_id      INT          NULL COMMENT '相册ID',
    files_name    VARCHAR(255) NULL COMMENT '文件路径',
    original_name VARCHAR(255) NULL COMMENT '原始文件名',
    files_size    BIGINT       NULL DEFAULT 0 COMMENT '文件大小(字节)',
    files_width   INT          NULL COMMENT '图片宽度',
    files_height  INT          NULL COMMENT '图片高度',
    album_type    INT          NULL DEFAULT 1 COMMENT '文件类型:1图片,2视频,3音频',
    is_system     INT          NULL DEFAULT 0 COMMENT '是否系统图片:0否,1是',
    store_id      INT          NULL DEFAULT 0 COMMENT '店铺ID',
    upload_time   DATETIME     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_album_id (album_id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='相册文件表';

-- 插入默认数据
-- 默认相册
INSERT INTO t_album (album_id, album_name, parent_id) VALUES (1, '默认相册', 0) ON DUPLICATE KEY UPDATE album_name = '默认相册';

-- 默认商品类目
INSERT INTO t_category (category_id, category_name, parent_id, deep, category_sort) VALUES 
(1, '电子产品', 0, 1, 1),
(2, '手机', 1, 2, 1),
(3, '电脑', 1, 2, 2),
(4, '服装', 0, 1, 2),
(5, '男装', 4, 2, 1),
(6, '女装', 4, 2, 2)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================
-- 商品管理菜单配置（必须与前端的router/index.js路径严格一致）
-- ============================================

-- 商品管理主菜单
INSERT INTO t_admin_menu (id, group_id, name, parent_id, permission, title, url) VALUES 
(100, 1, 'goods', 0, 'goods:manage', '商品管理', NULL)
ON DUPLICATE KEY UPDATE title = VALUES(title), url = VALUES(url);

-- 商品分类菜单（URL必须与前端路由 /category/list 严格一致）
INSERT INTO t_admin_menu (id, group_id, name, parent_id, permission, title, url) VALUES 
(101, 1, 'category', 100, 'category:view', '商品分类', '/category/list')
ON DUPLICATE KEY UPDATE title = VALUES(title), url = VALUES(url);

-- 商品管理菜单
INSERT INTO t_admin_menu (id, group_id, name, parent_id, permission, title, url) VALUES 
(102, 1, 'goods-common', 100, 'goods:view', '商品管理', '/goods-common/list')
ON DUPLICATE KEY UPDATE title = VALUES(title), url = VALUES(url);

-- 商品属性菜单
INSERT INTO t_admin_menu (id, group_id, name, parent_id, permission, title, url) VALUES 
(103, 1, 'attribute', 100, 'attribute:view', '商品属性', '/attribute/list')
ON DUPLICATE KEY UPDATE title = VALUES(title), url = VALUES(url);

-- 相册管理菜单
INSERT INTO t_admin_menu (id, group_id, name, parent_id, permission, title, url) VALUES 
(104, 1, 'album', 100, 'album:view', '相册管理', '/album/list')
ON DUPLICATE KEY UPDATE title = VALUES(title), url = VALUES(url);

-- ============================================
-- 数据库升级脚本：支持多类目
-- ============================================
-- 修改 category_id 字段类型为 VARCHAR(255) 以支持多类目存储
ALTER TABLE t_goods_common MODIFY COLUMN category_id VARCHAR(255) NULL COMMENT '类目ID列表，多个用逗号分隔';
