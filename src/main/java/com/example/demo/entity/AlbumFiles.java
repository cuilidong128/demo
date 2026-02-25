package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 相册文件实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_album_files")
public class AlbumFiles implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(value = "files_id", type = IdType.AUTO)
    private Integer filesId;

    /**
     * 相册ID
     */
    private Integer albumId;

    /**
     * 文件路径
     */
    private String filesName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小(字节)
     */
    private Long filesSize;

    /**
     * 图片宽度
     */
    private Integer filesWidth;

    /**
     * 图片高度
     */
    private Integer filesHeight;

    /**
     * 文件类型:1图片,2视频,3音频
     */
    private Integer albumType;

    /**
     * 店铺ID
     */
    private Integer storeId;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 是否系统图片:0否,1是
     */
    private Integer isSystem;
}
