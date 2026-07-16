package com.smart.nursing.common.exception;

import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.result.CommonResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException e) {
        log.error("[business exception] code={}, msg={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 AI 异常
     */
    @ExceptionHandler(AiException.class)
    public CommonResult<?> aiExceptionHandler(AiException e) {
        log.error("[ai exception] code={}, msg={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 @RequestBody + @Valid 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("[method argument not valid] {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理 @RequestParam + @Validated 校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.error("[constraint violation] {}", e.getMessage());
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("[missing servlet request parameter] {}", e.getMessage());
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                "请求参数缺失：" + e.getParameterName());
    }

    /**
     * 兜底处理其它所有异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<?> exceptionHandler(Exception e) {
        // 禁止 printStackTrace，统一使用日志记录
        log.error("[unknown exception]", e);
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.toErrorCode());
    }
}
