package com.smart.nursing.vo;

import com.smart.nursing.entity.TagEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 视频详情 VO
 */
@Data
public class VideoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 标题
     */
    private String title;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 描述
     */
    private String description;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 标签列表
     */
    private List<TagEntity> tagList;

    /**
     * 是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 学习进度
     */
    private Map<String, Object> progress;
}
