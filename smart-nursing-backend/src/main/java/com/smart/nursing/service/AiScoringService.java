package com.smart.nursing.service;

import com.smart.nursing.vo.AiScoreResult;

/**
 * AI 评分 Service（解答题评分）
 */
public interface AiScoringService {

    /**
     * AI 评分解答题
     *
     * @param questionContent 题干
     * @param referenceAnswer 参考答案/评分要点
     * @param studentAnswer   学生答案
     * @param fullScore       满分
     * @return 评分结果（得分+反馈+是否需人工复核）
     */
    AiScoreResult scoreAnswer(String questionContent, String referenceAnswer, String studentAnswer, int fullScore);
}
