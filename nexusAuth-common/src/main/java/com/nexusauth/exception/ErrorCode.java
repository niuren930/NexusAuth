package com.nexusauth.exception;

/**
 * 统一错误码定义接口
 *
 * @author niuren
 * @date 2026-10-04 08:50
 */
public interface ErrorCode {

    /**
     * 获取业务错误码。
     */
    Integer getCode();

    /**
     * 获取错误信息。
     */
    String getMessage();

}
