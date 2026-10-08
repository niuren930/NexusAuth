package com.nexusauth.client;

import com.nexusauth.api.system.SystemPermissionApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * System 当前上下文权限查询客户端
 *
 * @author niuren
 * @date 2026-10-08 22:16
 */
@FeignClient(name = "nexusauth-system", contextId = "systemPermissionClient")
public interface SystemPermissionClient extends SystemPermissionApi {
}