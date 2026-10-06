package com.nexusauth.exception;

/**
 * System 模块业务错误码
 * <p>
 * 3 表示 System 模块，002 表示租户授权查询场景
 *
 * @author niuren
 * @date 2026-10-06 21:56
 */
public enum SystemErrorCode implements ErrorCode {

    /**
     * 查询缺少或携带非法用户、租户上下文。
     */
    PERMISSION_CONTEXT_INVALID(3002000, "权限查询上下文无效"),

    /**
     * 当前用户或成员、租户状态不允许访问。
     */
    TENANT_ACCESS_DENIED(3002001, "当前用户不可访问租户");

    private final Integer code;
    private final String message;

    SystemErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
