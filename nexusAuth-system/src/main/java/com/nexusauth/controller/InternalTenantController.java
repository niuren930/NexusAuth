package com.nexusauth.controller;

import com.nexusauth.api.system.SystemTenantApi;
import com.nexusauth.api.system.dto.UserTenantInfo;
import com.nexusauth.service.TenantAccessService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户和租户内部接口
 *
 * @author niuren
 * @date 2026-10-05 11:47
 */
@RestController
public class InternalTenantController implements SystemTenantApi {

    private final TenantAccessService tenantAccessService;

    public InternalTenantController(TenantAccessService tenantAccessService) {
        this.tenantAccessService = tenantAccessService;
    }

    @Override
    public List<UserTenantInfo> getUserTenants(
            Long userId) {
        return tenantAccessService.getUserTenants(userId);
    }

    @Override
    public UserTenantInfo getTenantAccessInfo(Long userId, Long tenantId) {
        return tenantAccessService.getTenantAccessInfo(userId, tenantId);
    }

    @Override
    public UserTenantInfo getCurrentTenantAccessInfo() {
        return tenantAccessService.getCurrentTenantAccessInfo();
    }
}