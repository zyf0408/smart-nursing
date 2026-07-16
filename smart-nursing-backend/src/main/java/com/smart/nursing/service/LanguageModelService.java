package com.smart.nursing.service;

/**
 * 大语言模型文本生成 Service（同步调用）
 */
public interface LanguageModelService {

    /**
     * 同步文本生成
     *
     * @param prompt 提示词
     * @return 模型生成的文本
     */
    String generateText(String prompt);
}
