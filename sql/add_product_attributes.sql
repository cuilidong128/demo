-- ============================================
-- 添加商品规格和属性值数据
-- 用于支持多规格商品（尺码、颜色分类、年龄）
-- ============================================

-- ============================================
-- 数据库字段升级：添加SKU成本价字段
-- ============================================
ALTER TABLE t_goods ADD COLUMN IF NOT EXISTS goods_cost_price DECIMAL(19, 2) NULL DEFAULT 0.00 COMMENT '成本价' AFTER goods_market_price;

-- ============================================
-- 添加规格（属性）
-- ============================================
INSERT INTO t_attribute (attribute_id, attribute_name, attribute_sort, is_show) VALUES
(1, '尺码', 1, 1),
(2, '颜色分类', 2, 1),
(3, '年龄', 3, 1)
ON DUPLICATE KEY UPDATE 
    attribute_name = VALUES(attribute_name),
    attribute_sort = VALUES(attribute_sort),
    is_show = VALUES(is_show);

-- ============================================
-- 添加尺码属性值
-- ============================================
INSERT INTO t_attribute_value (attribute_value_id, attribute_id, attribute_value_name) VALUES
(1, 1, 'S'),
(2, 1, 'M'),
(3, 1, 'L'),
(4, 1, 'XL')
ON DUPLICATE KEY UPDATE 
    attribute_value_name = VALUES(attribute_value_name);

-- ============================================
-- 添加颜色分类属性值
-- ============================================
INSERT INTO t_attribute_value (attribute_value_id, attribute_id, attribute_value_name) VALUES
(5, 2, '卡其')
ON DUPLICATE KEY UPDATE 
    attribute_value_name = VALUES(attribute_value_name);

-- ============================================
-- 添加年龄属性值
-- ============================================
INSERT INTO t_attribute_value (attribute_value_id, attribute_id, attribute_value_name) VALUES
(6, 3, '中年'),
(7, 3, '少年'),
(8, 3, '老年')
ON DUPLICATE KEY UPDATE 
    attribute_value_name = VALUES(attribute_value_name);

-- ============================================
-- 验证数据
-- ============================================
SELECT '属性表数据' as info;
SELECT * FROM t_attribute ORDER BY attribute_sort;

SELECT '尺码属性值' as info;
SELECT * FROM t_attribute_value WHERE attribute_id = 1;

SELECT '颜色分类属性值' as info;
SELECT * FROM t_attribute_value WHERE attribute_id = 2;

SELECT '年龄属性值' as info;
SELECT * FROM t_attribute_value WHERE attribute_id = 3;

SELECT 't_goods表结构' as info;
SHOW COLUMNS FROM t_goods;
