package com.smart.nursing.dto;

import com.smart.nursing.common.result.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 学习计划查询/保存 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudyPlanDto extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 状态
     */
    private Integer status;
}
