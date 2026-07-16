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
 * 考试记录实体
 */
@Data
@ToString
@TableName("nursing_exam_record")
public class ExamRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
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
     * 考试次数
     */
    private Integer attemptCount;

    /**
     * 状态（0-进行中 1-已提交）
     */
    private Integer status;

    /**
     * 答题内容（JSON）
     */
    private String answers;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 是否及格（0-未及格 1-及格）
     */
    private Integer isPass;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
