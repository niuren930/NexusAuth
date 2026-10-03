package com.nexusauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NexusAuth 系统管理服务启动类
 */
@SpringBootApplication
@MapperScan("com.nexusauth.mapper")
public class NexusAuthSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusAuthSystemApplication.class, args);
    }
}
