package com.nexusauth.exception;

/**
 * 当前权限查询中的用户、租户或成员已失效，用于映射 HTTP 403
 *
 * @author niuren
 * @date 2026-10-08 22:18
 */
public class PermissionAccessDeniedException extends BusinessException {

    public PermissionAccessDeniedException() {
        super(AuthErrorCode.TENANT_ACCESS_DENIED);
    }
}
