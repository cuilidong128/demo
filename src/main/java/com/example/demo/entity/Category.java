package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品类目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类目ID
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    /**
     * 移动端图片
     */
    private String appImage;

    /**
     * 类目名称
     */
    private String categoryName;

    /**
     * 排序
     */
    private Integer categorySort;

    /**
     * 层级深度(1-3)
     */
    private Integer deep;

    /**
     * 父级ID,0为顶级
     */
    private Integer parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
