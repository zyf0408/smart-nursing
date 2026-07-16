package com.smart.nursing.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Token 校验切面
 * <p>
 * 切入 controller 包下所有方法：
 * <ol>
 *     <li>检查方法是否标注 @NoToken，有则直接放行</li>
 *     <li>从请求头获取 Token，并在 Redis 中校验，为空或无效抛 BusinessException(UNAUTHORIZED)</li>
 *     <li>滑动续期：每次请求刷新 Token 过期时间（10 小时）</li>
 *     <li>角色校验：/admin/ 路径需 ADMIN 或 TEACHER 角色，/mobile/ 路径需 NURSE 角色</li>
 * </ol>
 * 使用 Jackson ObjectMapper 序列化/反序列化（替代 FastJSON），返回 Object。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AroundCut {

    /**
     * 请求头中的 Token 字段名
     */
    private static final String TOKEN_HEADER = "token";

    /**
     * Redis 中 Token 的 key 前缀
     */
    private static final String TOKEN_PREFIX = "token:";

    /**
     * Token 过期时间（小时）
     */
    private static final long TOKEN_EXPIRE_HOURS = 10;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.smart.nursing.controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        // 1. 检查 @NoToken 注解，有则放行
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(NoToken.class)) {
            return joinPoint.proceed();
        }

        // 2. 从 Header 取 Token
        String token = request.getHeader(TOKEN_HEADER);
        if (token == null || token.isEmpty()) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 3. Redis 验证 Token
        String redisKey = TOKEN_PREFIX + token;
        String loginUserJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (loginUserJson == null || loginUserJson.isEmpty()) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 使用 Jackson ObjectMapper 反序列化（替代 FastJSON）
        LoginUser loginUser = objectMapper.readValue(loginUserJson, LoginUser.class);

        // 4. 角色校验
        String uri = request.getRequestURI();
        String role = loginUser.getRole();
        if (uri.contains("/admin/")) {
            // /admin/ 路径需 ADMIN 或 TEACHER 角色
            if (!"ADMIN".equals(role) && !"TEACHER".equals(role)) {
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN);
            }
        } else if (uri.contains("/mobile/")) {
            // /mobile/ 路径需 NURSE 角色
            if (!"NURSE".equals(role)) {
                throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN);
            }
        }

        // 5. 将当前登录用户存入 request 属性，供 Controller 获取
        request.setAttribute("loginUser", loginUser);

        // 6. 滑动续期：每次请求刷新 Token 过期时间
        stringRedisTemplate.expire(redisKey, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        // 7. 放行，返回 Object（不强转 CommonResult）
        return joinPoint.proceed();
    }
}
