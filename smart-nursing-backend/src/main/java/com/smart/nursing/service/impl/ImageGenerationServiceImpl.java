package com.smart.nursing.service.impl;

import com.smart.nursing.service.ImageGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文生图 Service 实现类
 * <p>
 * 当前 DashScope 的 OpenAI 兼容模式可能不支持图片生成接口，
 * 因此暂时返回提示信息。如需实现，需调用 DashScope 原生 API
 * （例如通义万相 wanx 系列模型，接口地址：
 * https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis ）。
 */
@Slf4j
@Service
public class ImageGenerationServiceImpl implements ImageGenerationService {

    @Override
    public String generateImage(String prompt) {
        // TODO: 如需实现文生图，需调用 DashScope 原生 API（OpenAI 兼容模式不支持图片生成）
        log.info("[AI 文生图] 收到生成请求, prompt={}", prompt);
        return "文生图功能正在开发中";
    }
}
