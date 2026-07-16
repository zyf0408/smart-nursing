package com.smart.nursing.dto;

import com.smart.nursing.common.result.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 学习记录查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LearningRecordDto extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容类型（1-文章 2-视频 3-课件）
     */
    private Integer contentType;

    /**
     * 内容ID
     */
    private Long contentId;
}
