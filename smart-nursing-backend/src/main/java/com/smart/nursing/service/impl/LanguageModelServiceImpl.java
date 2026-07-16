package com.smart.nursing.service.impl;

import com.smart.nursing.common.exception.AiException;
import com.smart.nursing.service.LanguageModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

/**
 * 大语言模型文本生成 Service 实现类（同步调用）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageModelServiceImpl implements LanguageModelService {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public String generateText(String prompt) {
        try {
            Prompt promptObj = new Prompt(prompt);
            return openAiChatModel.call(promptObj).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("[AI 同步生成] 调用大模型失败, prompt={}", prompt, e);
            throw new AiException(500, "AI 服务暂时不可用，请稍后重试");
        }
    }
}
