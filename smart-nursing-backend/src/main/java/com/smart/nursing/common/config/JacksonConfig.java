package com.smart.nursing.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

/**
 * Jackson 配置
 * <p>
 * 将 Long 和 BigInteger 序列化为 String，避免前端 JavaScript 精度丢失。
 * <p>
 * 原因：MyBatis-Plus 默认使用雪花算法生成 ID（19 位长整型），
 * 超过 JavaScript Number.MAX_SAFE_INTEGER（2^53 - 1 = 9007199254740991，16 位），
 * 前端 JS 解析时会发生精度丢失，导致删除/更新时传入错误 ID。
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            // Long 类型（64 位整数）序列化为 String
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            // BigInteger 类型也序列化为 String
            module.addSerializer(BigInteger.class, ToStringSerializer.instance);
            // 使用 modulesToInstall 而非 modules，避免覆盖 JavaTimeModule 等默认模块
            builder.modulesToInstall(module);
        };
    }
}
