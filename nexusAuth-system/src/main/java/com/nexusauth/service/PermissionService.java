package com.nexusauth.service;

import com.nexusauth.api.system.dto.UserPermissionInfo;

/**
 * 当前用户的 RBAC 授权事实查询服务
 *
 * @author niuren
 * @date 2026-10-06 21:58
 */
public interface PermissionService {

    /**
     * 查询当前用户在当前租户的授权信息。
     * userId、tenantId 从经过校验的上下文取得，调用方不指定任意查询目标。
     */
    UserPermissionInfo getCurrentUserPermissionInfo();
}
