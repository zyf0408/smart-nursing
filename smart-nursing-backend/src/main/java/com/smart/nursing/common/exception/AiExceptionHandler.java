package com.smart.nursing.common.exception;

import com.smart.nursing.common.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * AI 异常处理器
 * <p>
 * 专门处理 SpringAI 调用过程中抛出的异常（OpenAI 客户端异常、瞬时/非瞬时 AI 异常等），
 * 返回统一的友好提示信息。优先级高于 {@link GlobalExceptionHandler}，
 * 确保这些具体的 AI 异常能被精确匹配。
 * <p>
 * 对于 Service 层已包装为 {@link AiException} 的异常，仍由
 * {@link GlobalExceptionHandler#aiExceptionHandler(AiException)} 处理。
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class AiExceptionHandler {

    /** AI 服务不可用统一提示 */
    private static final String AI_UNAVAILABLE_MSG = "AI 服务暂时不可用，请稍后重试";

    /**
     * 处理 OpenAI 客户端异常（鉴权失败、请求格式错误、模型不可用等）
     */
    @ExceptionHandler(OpenAiApiClientErrorException.class)
    public CommonResult<?> openAiApiClientErrorExceptionHandler(OpenAiApiClientErrorException e) {
        log.error("[AI OpenAI client error] {}", e.getMessage());
        return CommonResult.error(500, AI_UNAVAILABLE_MSG);
    }

    /**
     * 处理非瞬时 AI 异常（不可重试的错误，如内容被拒绝等）
     */
    @ExceptionHandler(NonTransientAiException.class)
    public CommonResult<?> nonTransientAiExceptionHandler(NonTransientAiException e) {
        log.error("[AI non-transient exception] {}", e.getMessage());
        return CommonResult.error(500, AI_UNAVAILABLE_MSG);
    }

    /**
     * 处理瞬时 AI 异常（可重试的错误，如限流、网络抖动等）
     */
    @ExceptionHandler(TransientAiException.class)
    public CommonResult<?> transientAiExceptionHandler(TransientAiException e) {
        log.error("[AI transient exception] {}", e.getMessage());
        return CommonResult.error(500, AI_UNAVAILABLE_MSG);
    }
}
