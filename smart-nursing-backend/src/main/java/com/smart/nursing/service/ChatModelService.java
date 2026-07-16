package com.smart.nursing.service;

import reactor.core.publisher.Flux;

/**
 * 流式对话 Service
 */
public interface ChatModelService {

    /**
     * 流式对话（携带历史上下文）
     *
     * @param sessionId 会话ID（用于隔离不同用户的对话历史）
     * @param message   用户本次输入
     * @return 流式返回的文本片段
     */
    Flux<String> streamChat(String sessionId, String message);
}
