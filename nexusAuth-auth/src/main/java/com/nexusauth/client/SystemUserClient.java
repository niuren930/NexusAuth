package com.nexusauth.client;

import com.nexusauth.api.system.SystemUserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * System 用户服务 Feign Client。
 *
 * @author niuren
 * @date 2026-10-03 22:14
 */
@FeignClient(
        name = "nexusauth-system",
        contextId = "systemUserClient"
)
public interface SystemUserClient extends SystemUserApi {
}
