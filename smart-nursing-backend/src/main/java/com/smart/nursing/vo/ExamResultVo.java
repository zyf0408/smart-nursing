package com.smart.nursing.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试结果 VO（含答题详情）
 */
@Data
public class ExamResultVo implements Serializable {

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
     * 考试名称
     */
    private String examName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 是否及格（0-未及格 1-及格）
     */
    private Integer isPass;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 答题详情列表
     */
    private List<Map<String, Object>> answerDetails;
}
