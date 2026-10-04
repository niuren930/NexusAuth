package com.nexusauth.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.nexusauth.core.Result;
import com.nexusauth.exception.AuthErrorCode;
import com.nexusauth.exception.BusinessException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Auth 服务全局异常处理器。
 *
 * @author niuren
 * @date 2026-10-04 09:40
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        return Result.fail(
                exception.getCode(),
                exception.getMessage()
        );
    }

    /**
     * Sa-Token 未登录异常。
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<Void>> handleNotLoginException(
            NotLoginException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        Result.fail(
                                AuthErrorCode.NOT_LOGIN.getCode(),
                                AuthErrorCode.NOT_LOGIN.getMessage()
                        )
                );
    }

    /**
     * Feign 未处理异常。
     *
     * 正常情况下业务层应该尽量转换，
     * 这里作为最后兜底。
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Result<Void>> handleFeignException(
            FeignException exception) {

        log.error(
                "远程服务调用异常",
                exception
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        Result.fail(
                                AuthErrorCode.USER_SERVICE_UNAVAILABLE.getCode(),
                                AuthErrorCode.USER_SERVICE_UNAVAILABLE.getMessage()
                        )
                );
    }

    /**
     * 未知系统异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(
            Exception exception) {

        log.error(
                "未知系统异常",
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Result.fail(
                                500000,
                                "系统内部异常"
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(
                        AuthErrorCode.INVALID_REQUEST.getMessage()
                );

        return ResponseEntity
                .badRequest()
                .body(
                        Result.fail(
                                AuthErrorCode.INVALID_REQUEST.getCode(),
                                message
                        )
                );
    }
}
