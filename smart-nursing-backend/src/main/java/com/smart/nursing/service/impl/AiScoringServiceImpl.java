package com.smart.nursing.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.nursing.service.AiScoringService;
import com.smart.nursing.service.LanguageModelService;
import com.smart.nursing.vo.AiScoreResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 评分 Service 实现类（调用通义千问对解答题进行语义评分）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiScoringServiceImpl implements AiScoringService {

    private final LanguageModelService languageModelService;
    private final ObjectMapper objectMapper;

    @Override
    public AiScoreResult scoreAnswer(String questionContent, String referenceAnswer, String studentAnswer, int fullScore) {
        AiScoreResult result = new AiScoreResult();
        result.setScore(0);
        result.setNeedManualReview(false);

        // 未作答直接0分
        if (studentAnswer == null || studentAnswer.trim().isEmpty()) {
            result.setFeedback("未作答");
            return result;
        }

        try {
            String prompt = buildPrompt(questionContent, referenceAnswer, studentAnswer, fullScore);
            String aiResponse = languageModelService.generateText(prompt);
            return parseAiResponse(aiResponse, fullScore);
        } catch (Exception e) {
            log.error("[AI 评分] 调用失败，降级为待人工评分。题干={}, 学生答案={}", questionContent, studentAnswer, e);
            result.setScore(0);
            result.setFeedback("AI 评分服务不可用，待人工评分");
            result.setNeedManualReview(true);
            return result;
        }
    }

    /**
     * 构造评分 prompt
     */
    private String buildPrompt(String questionContent, String referenceAnswer, String studentAnswer, int fullScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位护理学考试评分专家。请根据以下信息对学生的解答进行评分。\n\n");
        sb.append("题目：").append(questionContent).append("\n");
        sb.append("参考答案/评分要点：").append(referenceAnswer != null ? referenceAnswer : "无").append("\n");
        sb.append("学生答案：").append(studentAnswer).append("\n");
        sb.append("满分：").append(fullScore).append("分\n\n");
        sb.append("评分要求：\n");
        sb.append("1. 根据学生答案与参考答案的匹配度、知识点的完整性和准确性评分\n");
        sb.append("2. 得分为0到").append(fullScore).append("之间的整数\n");
        sb.append("3. feedback简要说明得分理由（不超过100字）\n");
        sb.append("4. 严格按以下JSON格式返回，不要包含markdown代码块或其他内容：\n");
        sb.append("{\"score\": 得分, \"feedback\": \"评分说明\"}");
        return sb.toString();
    }

    /**
     * 解析 AI 返回的 JSON
     */
    private AiScoreResult parseAiResponse(String aiResponse, int fullScore) {
        AiScoreResult result = new AiScoreResult();
        result.setNeedManualReview(false);
        try {
            String json = extractJson(aiResponse);
            JsonNode node = objectMapper.readTree(json);
            int score = node.has("score") ? node.get("score").asInt() : 0;
            String feedback = node.has("feedback") ? node.get("feedback").asText() : "";
            // 边界保护
            score = Math.max(0, Math.min(score, fullScore));
            result.setScore(score);
            result.setFeedback(feedback);
        } catch (Exception e) {
            log.warn("[AI 评分] 解析AI返回失败，降级为待人工评分。response={}", aiResponse, e);
            result.setScore(0);
            result.setFeedback("AI评分结果解析失败，待人工评分");
            result.setNeedManualReview(true);
        }
        return result;
    }

    /**
     * 从 AI 返回中提取 JSON（兼容 markdown 代码块包裹）
     */
    private String extractJson(String response) {
        if (response == null) {
            return "{}";
        }
        String text = response.trim();
        // 去除 ```json ... ``` 包裹
        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            if (firstNewline > 0) {
                text = text.substring(firstNewline + 1);
            }
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
            text = text.trim();
        }
        // 提取第一个 { 到最后一个 }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
