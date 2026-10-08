package com.nexusauth.interceptor;

import com.nexusauth.constant.NexusHeaderConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.nexusauth.security.InternalServiceCredential;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 向 System 注入服务凭证与已验证的请求上下文。
 *
 * 仅向 nexusauth-system 发送专用服务凭证，
 * 用户与租户来自 Auth 已恢复的上下文，不复制浏览器提交的内部 Header。
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
 * X-Nexus-Service-Token
 * X-Nexus-User-Id
 * X-Nexus-Tenant-Id
 *      ↓
 * nexusauth-system
 *
 * 注意：
 * 服务凭证用于 System 鉴别调用方；用户与租户 Header 仍需接受业务有效性校验。
 *
 * @author niuren
 * @date 2026-10-05 21:22
 */
@Component
public class NexusFeignContextInterceptor implements RequestInterceptor {

    private static final String SYSTEM_SERVICE = "nexusauth-system";

    private final InternalServiceCredential credential;

    public NexusFeignContextInterceptor(
            @Value("${nexusauth.internal.system-token}") String serviceToken) {
        this.credential = new InternalServiceCredential(serviceToken);
    }

    @Override
    public void apply(RequestTemplate template) {
        /*
         * 清除模板中已有的保留 Header，再从受信任来源重建。
         * 避免重复执行或其他模板设置追加多个值，也避免沿用过期身份。
         */
        template.removeHeader(NexusHeaderConstants.SERVICE_TOKEN);
        template.removeHeader(NexusHeaderConstants.USER_ID);
        template.removeHeader(NexusHeaderConstants.TENANT_ID);

        /* 全局 Feign 拦截器只为 System 注入凭证，防止泄露给其他目标服务。 */
        if (template.feignTarget() == null
                || !SYSTEM_SERVICE.equals(template.feignTarget().name())) {
            return;
        }

        /* 登录前尚无用户上下文，但查询认证信息同样需要验证服务身份。 */
        template.header(NexusHeaderConstants.SERVICE_TOKEN, credential.asHeaderValue());

        Long userId = OperatorContextHolder.getUserId();
        if (userId != null) {
            template.header(NexusHeaderConstants.USER_ID, String.valueOf(userId));
        }

        /* 已登录未选租户时不发送租户 Header，不补默认租户。 */
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            template.header(NexusHeaderConstants.TENANT_ID, String.valueOf(tenantId));
        }
    }
}
