package com.nexusauth.interceptor;

import com.nexusauth.constant.NexusHeaderConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign 上下文透传拦截器。
 *
 * Auth 服务调用其他内部服务时，
 * 将当前已经确认的用户和租户上下文写入内部请求头
 *
 * 调用链示例：
 *
 * AuthRequestContextFilter
 *      ↓
 * OperatorContextHolder
 * TenantContextHolder
 *      ↓
 * NexusFeignContextInterceptor
 *      ↓
 * X-Nexus-User-Id
 * X-Nexus-Tenant-Id
 *      ↓
 * nexusauth-system
 *
 * 注意：
 * 这里传递的是服务内部上下文，而不是客户端身份凭证。
 *
 * @author niuren
 * @date 2026-10-05 21:22
 */
@Component
public class NexusFeignContextInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        /**
         * 当前登录用户
         *
         * 登录接口调用 system 查询用户名时，此时如果用户还未登录，那 userId 可能为空
         */
        Long userId = OperatorContextHolder.getUserId();

        if (userId != null) {
            template.header(NexusHeaderConstants.USER_ID,String.valueOf(userId));
        }

        /*
         * 当前租户。
         *
         * 用户登录成功但尚未选择租户时，
         * tenantId 为空，因此不会发送租户 Header。
         */
        Long tenantId = TenantContextHolder.getTenantId();

        if (tenantId != null) {
            template.header(NexusHeaderConstants.TENANT_ID, String.valueOf(tenantId));
        }
    }
}
