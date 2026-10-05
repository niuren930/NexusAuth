package com.nexusauth.api.system;

import com.nexusauth.api.system.dto.UserTenantInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * System 租户内部接口
 *
 * @author niuren
 * @date 2026-10-05 11:00
 */
public interface SystemTenantApi {
    /**
     * 查询用户可以进入的全部租户。
     */
    @GetMapping("/internal/tenants/by-user")
    List<UserTenantInfo> getUserTenants(@RequestParam("userId") Long userId);

    /**
     * 查询并验证用户是否可以进入指定租户。
     */
    @GetMapping("/internal/tenants/access")
    UserTenantInfo getTenantAccessInfo(@RequestParam("userId") Long userId,
                                       @RequestParam("tenantId") Long tenantId
    );
}
