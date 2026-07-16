package com.smart.nursing.vo;

import com.smart.nursing.entity.TagEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 文章详情 VO
 */
@Data
public class ArticleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long articleId;

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
     * 摘要
     */
    private String summary;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

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
