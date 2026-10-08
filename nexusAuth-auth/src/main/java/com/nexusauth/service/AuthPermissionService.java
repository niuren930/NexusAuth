package com.nexusauth.service;

import com.nexusauth.api.system.dto.UserPermissionInfo;

/**
 * Auth 当前登录态下的权限查询服务
 *
 * @author niuren
 * @date 2026-10-08 22:19
 */
public interface AuthPermissionService {
    /**
     * 校验登录态与当前租户，取得并核对 System 返回的授权事实。
     */
    UserPermissionInfo getCurrentUserPermissionInfo();
}
