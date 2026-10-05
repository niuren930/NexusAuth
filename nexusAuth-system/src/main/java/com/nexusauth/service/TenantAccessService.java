package com.nexusauth.service;

import com.nexusauth.api.system.dto.UserTenantInfo;

import java.util.List;

/**
 * 租户访问服务
 *
 * @author niuren
 * @date 2026-10-05 11:04
 */
public interface TenantAccessService {
    /**
     * 根据用户id查询租户列表
     * @param userId 用户id
     * @return
     */
    List<UserTenantInfo> getUserTenants(Long userId);

    /**
     * 查询并验证用户是否可以进入指定租户
     * @param userId 用户id
     * @param tenantId 租户id
     * @return
     */
    UserTenantInfo getTenantAccessInfo(Long userId, Long tenantId);

    /**
     * 获取当前用户在当前租户中的成员信息。
     *
     * @return 当前租户访问关系；不存在时返回 null
     */
    UserTenantInfo getCurrentTenantAccessInfo();
}
