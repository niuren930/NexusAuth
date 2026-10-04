package com.nexusauth.core;

/**
 * NexusAuth 统一响应对象。
 *
 * @param code    业务状态码
 * @param message 响应信息
 * @param data    响应数据
 * @param <T>     数据类型
 *
 * @author niuren
 * @date 2026-10-04 08:49
 */
public record Result<T>(
        Integer code,
        String message,
        T data
) {

    private static final Integer SUCCESS_CODE = 0;

    private static final String SUCCESS_MESSAGE = "success";

    public static <T> Result<T> success(T data) {
        return new Result<>(
                SUCCESS_CODE,
                SUCCESS_MESSAGE,
                data
        );
    }

    public static Result<Void> success() {
        return new Result<>(
                SUCCESS_CODE,
                SUCCESS_MESSAGE,
                null
        );
    }

    public static <T> Result<T> fail(
            Integer code,
            String message) {

        return new Result<>(
                code,
                message,
                null
        );
    }
}