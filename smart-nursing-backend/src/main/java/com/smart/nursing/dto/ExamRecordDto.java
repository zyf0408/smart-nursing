package com.smart.nursing.dto;

import com.smart.nursing.common.result.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 考试记录查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamRecordDto extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（用于模糊查询）
     */
    private String username;

    /**
     * 是否及格
     */
    private Integer isPass;

    /**
     * 考试名称（用于模糊查询）
     */
    private String examName;
}
