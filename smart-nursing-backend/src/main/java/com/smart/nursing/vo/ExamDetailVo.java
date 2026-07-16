package com.smart.nursing.vo;

import com.smart.nursing.entity.QuestionEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试详情 VO（含试题列表）
 */
@Data
public class ExamDetailVo implements Serializable {

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
     * 状态
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
     * 试题列表
     */
    private List<QuestionEntity> questionList;
}
