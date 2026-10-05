package com.nexusauth.client;

import com.nexusauth.api.system.SystemTenantApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * System 用户租户 Feign Client。
 *
 * @author niuren
 * @date 2026-10-05 11:50
 */
@FeignClient(
        name = "nexusauth-system",
        contextId = "systemTenantClient"
)
public interface SystemTenantClient extends SystemTenantApi {
}
