package com.smart.nursing.vo;

import lombok.Data;

/**
 * 题目判分结果（支持客观题精确判分与解答题AI评分）
 */
@Data
public class QuestionScoreResult {

    /**
     * 实际得分
     */
    private int earnedScore;

    /**
     * 是否完全正确
     */
    private boolean correct;

    /**
     * 评分反馈（解答题AI反馈）
     */
    private String feedback;

    /**
     * 是否需要人工复核
     */
    private boolean needManualReview;
}
