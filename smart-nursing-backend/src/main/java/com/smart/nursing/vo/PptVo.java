package com.smart.nursing.vo;

import com.smart.nursing.entity.TagEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课件详情 VO
 */
@Data
public class PptVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课件ID
     */
    private Long pptId;

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
     * 文件地址
     */
    private String fileUrl;

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
}
