package com.nexusauth.exception;

/**
 * 业务异常
 *
 * @author niuren
 * @date 2026-10-04 08:50
 */
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public Integer getCode() {
        return code;
    }
}
