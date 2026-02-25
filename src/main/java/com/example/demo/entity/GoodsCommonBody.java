package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商品详情表实体类
 * 存储商品详情内容，前端使用富文本编辑器编辑
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_goods_common_body")
public class GoodsCommonBody implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SPU ID（主键）
     */
    @TableId(value = "common_id", type = IdType.INPUT)
    private Integer commonId;

    /**
     * 底部格式标识
     */
    private Integer formatBottom;

    /**
     * 顶部格式标识
     */
    private Integer formatTop;

    /**
     * PC端商品详情内容（富文本HTML）
     */
    private String goodsBody;

    /**
     * 移动端商品详情内容（富文本HTML）
     */
    private String mobileBody;
}
