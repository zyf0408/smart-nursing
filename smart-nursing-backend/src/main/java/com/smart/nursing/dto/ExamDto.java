package com.smart.nursing.dto;

import com.smart.nursing.common.result.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 考试查询/保存 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamDto extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 状态
     */
    private Integer status;
}
