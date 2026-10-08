package com.nexusauth.exception;

/**
 * 权限查询的上游服务故障，用于映射 HTTP 502
 *
 * @author niuren
 * @date 2026-10-08 22:17
 */
public class PermissionServiceUnavailableException extends BusinessException {

    public PermissionServiceUnavailableException() {
        super(AuthErrorCode.PERMISSION_SERVICE_UNAVAILABLE);
    }
}
