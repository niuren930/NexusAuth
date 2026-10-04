package com.nexusauth.exception;

/**
 * 认证模块错误码
 *
 * 错误码规律：
 *      2       = auth 模块
 *      001     = 登录认证场景
 *      001、002、003     = 具体错误
 *
 * @author niuren
 * @date 2026-10-04 09:05
 */
public enum AuthErrorCode implements ErrorCode {

    /**
     * 用户名或密码错误。
     * <p>
     * 用户不存在与密码错误统一返回此错误，
     * 避免暴露用户名是否存在。
     */
    USERNAME_OR_PASSWORD_ERROR(
            2001001,
            "用户名或密码错误"
    ),

    /**
     * 当前账号不允许登录。
     */
    ACCOUNT_NOT_ALLOWED(
            2001002,
            "账号当前不可登录"
    ),

    /**
     * 未登录或登录状态失效。
     */
    NOT_LOGIN(
            2001003,
            "登录状态已失效，请重新登录"
    ),

    /**
     * System 用户服务调用异常。
     */
    USER_SERVICE_UNAVAILABLE(
            2001501,
            "用户服务暂时不可用"
    );

    private final Integer code;

    private final String message;

    AuthErrorCode(
            Integer code,
            String message) {

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
