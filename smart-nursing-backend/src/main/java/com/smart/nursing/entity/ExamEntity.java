package com.smart.nursing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考试实体
 */
@Data
@ToString
@TableName("nursing_exam")
public class ExamEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 考试ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long examId;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 描述
     */
    private String description;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 及格分数
     */
    private Integer passScore;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 最大考试次数
     */
    private Integer maxAttempts;

    /**
     * 状态（0-未开始 1-进行中 2-已结束）
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除标识（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer isDelete;
}
