package com.smart.nursing.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 操作日志切面
 * <p>
 * 通过 @RecordLog 注解标记需要记录日志的方法，使用 @Around 覆盖正常与异常场景。
 * <ul>
 *     <li>安全序列化参数：过滤 MultipartFile / HttpServletRequest / HttpServletResponse / BindingResult / byte[] 等特殊对象</li>
 *     <li>截断超长参数（&gt;2000 字符）</li>
 *     <li>异常时也记录日志</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    /**
     * 参数最大长度，超过则截断
     */
    private static final int MAX_PARAM_LENGTH = 2000;

    private final ObjectMapper objectMapper;

    @Around("@annotation(recordLog)")
    public Object around(ProceedingJoinPoint joinPoint, RecordLog recordLog) throws Throwable {
        String operation = recordLog.value();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = buildParams(joinPoint);

        long startTime = System.currentTimeMillis();
        log.info("[操作日志] {} - {}.{} 开始, 参数: {}", operation, className, methodName, params);
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[操作日志] {} - {}.{} 完成, 耗时: {}ms", operation, className, methodName, costTime);
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            // 异常时也记录日志
            log.error("[操作日志] {} - {}.{} 异常, 耗时: {}ms, 异常: {}",
                    operation, className, methodName, costTime, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 安全构建参数字符串
     */
    private String buildParams(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        try {
            // 过滤特殊对象，避免序列化失败或输出无意义内容
            Object[] filteredArgs = Arrays.stream(args)
                    .map(this::filterArg)
                    .toArray();
            String json = objectMapper.writeValueAsString(filteredArgs);
            // 截断超长参数
            if (json.length() > MAX_PARAM_LENGTH) {
                json = json.substring(0, MAX_PARAM_LENGTH) + "...";
            }
            return json;
        } catch (Exception e) {
            log.warn("[操作日志] 参数序列化失败: {}", e.getMessage());
            return Arrays.toString(args);
        }
    }

    /**
     * 过滤无法或无需序列化的特殊对象
     */
    private Object filterArg(Object arg) {
        if (arg == null) {
            return null;
        }
        if (arg instanceof MultipartFile
                || arg instanceof HttpServletRequest
                || arg instanceof HttpServletResponse
                || arg instanceof BindingResult
                || arg instanceof byte[]) {
            return "[" + arg.getClass().getSimpleName() + "]";
        }
        return arg;
    }
}
