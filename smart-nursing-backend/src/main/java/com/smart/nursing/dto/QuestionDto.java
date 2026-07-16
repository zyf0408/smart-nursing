package com.smart.nursing.dto;

import com.smart.nursing.common.result.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 试题查询/保存 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDto extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 试题ID
     */
    private Long questionId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 题型
     */
    private Integer questionType;

    /**
     * 题干内容
     */
    private String content;
}
