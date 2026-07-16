package com.smart.nursing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧护理培训系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.smart.nursing.dao")
public class SmartNursingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartNursingApplication.class, args);
    }
}
