package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 商品SPU关联音频/视频表实体类
 * 存储商品关联的视频信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("goods_video")
public class GoodsVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频ID
     */
    @TableId(value = "video_id", type = IdType.AUTO)
    private Integer videoId;

    /**
     * 商品SPU ID
     */
    private Integer commonId;

    /**
     * 视频名称/路径
     */
    private String videoName;

    /**
     * 视频排序
     */
    private Integer videoSort;

    /**
     * 腾讯云COS文件ID
     */
    private String videoCosFilesId;

    /**
     * 是否默认视频：0否，1是
     */
    private Integer isDefault;
}
