package com.smart.nursing.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 配置
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "智慧护理培训系统 API",
                version = "1.0.0",
                description = "智慧护理培训系统后端接口文档",
                contact = @Contact(name = "Smart Nursing", email = "admin@smart-nursing.com"),
                license = @License(name = "Apache 2.0")
        )
)
public class SpringDocConfig {
}
