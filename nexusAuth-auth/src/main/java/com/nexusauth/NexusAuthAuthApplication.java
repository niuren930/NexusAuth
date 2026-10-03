package com.nexusauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * NexusAuth 统一认证授权服务（SSO）启动类
 */
@EnableFeignClients // 开启 Feign
@SpringBootApplication
public class NexusAuthAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusAuthAuthApplication.class, args);
    }
}
