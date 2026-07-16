package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习计划实体
 */
@Data
@ToString
@TableName("nursing_study_plan")
public class StudyPlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 目标内容类型（1-文章 2-视频 3-课件）
     */
    private Integer targetContentType;

    /**
     * 目标内容ID
     */
    private Long targetContentId;

    /**
     * 目标进度（0-100）
     */
    private Integer targetProgress;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 状态（0-未开始 1-进行中 2-已完成）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
