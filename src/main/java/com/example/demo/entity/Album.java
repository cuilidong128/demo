package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 相册实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 相册ID
     */
    @TableId(value = "album_id", type = IdType.AUTO)
    private Integer albumId;

    /**
     * 相册名称
     */
    private String albumName;

    /**
     * 父级ID,0为顶级
     */
    private Integer parentId;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
