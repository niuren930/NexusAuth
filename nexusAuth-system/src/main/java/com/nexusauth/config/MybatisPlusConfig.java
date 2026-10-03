package com.nexusauth.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.nexusauth.handler.tenant.NexusTenantLineHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置。
 *
 * @author niuren
 * @date 2026-10-03 18:33
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 内部插件。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            NexusTenantLineHandler tenantLineHandler) {

        MybatisPlusInterceptor interceptor =
                new MybatisPlusInterceptor();

        // 多租户插件
        TenantLineInnerInterceptor tenantInterceptor =
                new TenantLineInnerInterceptor();

        tenantInterceptor.setTenantLineHandler(
                tenantLineHandler
        );

        interceptor.addInnerInterceptor(
                tenantInterceptor
        );

        return interceptor;
    }
}