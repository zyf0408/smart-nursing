package com.smart.nursing.vo;

import lombok.Data;

/**
 * AI 评分结果
 */
@Data
public class AiScoreResult {

    /**
     * 得分
     */
    private int score;

    /**
     * 评分反馈
     */
    private String feedback;

    /**
     * 是否需要人工复核（AI不可用时为true）
     */
    private boolean needManualReview;
}
