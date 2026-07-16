package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 视频-标签关联实体
 */
@Data
@ToString
@TableName("nursing_video_tag")
public class VideoTagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 标签ID
     */
    private Long tagId;
}
