package com.smart.nursing.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.nursing.entity.LogEntity;
import com.smart.nursing.service.ILogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
 *     <li>将操作日志持久化到 sys_log 表</li>
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
    private final ILogService logService;

    @Around("@annotation(recordLog)")
    public Object around(ProceedingJoinPoint joinPoint, RecordLog recordLog) throws Throwable {
        String operation = recordLog.value();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = buildParams(joinPoint);

        // 提前获取请求信息（异步保存时 RequestContextHolder 可能已清空）
        HttpServletRequest request = getRequest();
        String username = extractUsername(request);
        String ip = extractIp(request);
        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String url = request != null ? request.getRequestURI() : "";

        long startTime = System.currentTimeMillis();
        log.info("[操作日志] {} - {}.{} 开始, 参数: {}", operation, className, methodName, params);
        try {
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[操作日志] {} - {}.{} 完成, 耗时: {}ms", operation, className, methodName, costTime);
            // 异步保存日志到数据库（成功日志）
            saveLog(operation, httpMethod, url, params, username, ip, costTime, 1);
            return result;
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            // 异常时也记录日志
            log.error("[操作日志] {} - {}.{} 异常, 耗时: {}ms, 异常: {}",
                    operation, className, methodName, costTime, e.getMessage(), e);
            // 异步保存日志到数据库（异常日志）
            saveLog(operation, httpMethod, url, params + " | 异常: " + e.getMessage(),
                    username, ip, costTime, 2);
            throw e;
        }
    }

    /**
     * 异步保存日志到数据库
     *
     * @param operation  操作描述
     * @param method     请求方法 GET/POST/PUT/DELETE
     * @param url        请求地址
     * @param params     请求参数
     * @param username   操作用户名
     * @param ip         请求 IP
     * @param costTime   耗时（ms）
     * @param logType    日志类型：1-正常，2-异常
     */
    private void saveLog(String operation, String method, String url, String params,
                         String username, String ip, long costTime, int logType) {
        try {
            LogEntity logEntity = new LogEntity();
            logEntity.setUsername(username);
            logEntity.setOperation(operation);
            logEntity.setMethod(method);
            logEntity.setUrl(url);
            logEntity.setParams(params);
            logEntity.setIp(ip);
            logEntity.setCostTime(costTime);
            logEntity.setLogType(logType);
            // createTime 由 MyBatis-Plus 自动填充
            logService.save(logEntity);
        } catch (Exception e) {
            // 日志保存失败不影响业务
            log.warn("[操作日志] 保存到数据库失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 从 request 属性中提取用户名（AroundCut 已放入 loginUser）
     */
    private String extractUsername(HttpServletRequest request) {
        if (request == null) return "system";
        try {
            Object loginUser = request.getAttribute("loginUser");
            if (loginUser != null) {
                // 通过反射获取 username 字段，避免循环依赖
                return (String) loginUser.getClass().getMethod("getUsername").invoke(loginUser);
            }
        } catch (Exception ignored) {
        }
        return "anonymous";
    }

    /**
     * 提取客户端 IP
     */
    private String extractIp(HttpServletRequest request) {
        if (request == null) return "";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
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
