package com.smart.nursing.service;

/**
 * 文生图 Service
 */
public interface ImageGenerationService {

    /**
     * 根据提示词生成图片
     *
     * @param prompt 图片描述提示词
     * @return 生成结果（图片地址或提示信息）
     */
    String generateImage(String prompt);
}
