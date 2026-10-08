package com.nexusauth.controller;

import com.nexusauth.exception.BusinessException;
import com.nexusauth.exception.SystemErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 当前内部 Controller 的业务异常与 HTTP 状态映射
 *
 * 只处理 BusinessException；用户不存在的现有 HTTP 404 契约继续保留
 *
 * @author niuren
 * @date 2026-10-08 22:03
 */
@RestControllerAdvice(assignableTypes = {
        InternalUserController.class,
        InternalTenantController.class,
        InternalPermissionController.class
})
public class InternalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException exception) {
        Integer code = exception.getCode();
        HttpStatus status;
        String detail;

        if (Objects.equals(code, SystemErrorCode.OPERATOR_CONTEXT_INVALID.getCode())
                || Objects.equals(code, SystemErrorCode.PERMISSION_CONTEXT_INVALID.getCode())) {
            status = HttpStatus.BAD_REQUEST;
            detail = exception.getMessage();
        } else if (Objects.equals(code, SystemErrorCode.CURRENT_USER_MISMATCH.getCode())
                || Objects.equals(code, SystemErrorCode.TENANT_ACCESS_DENIED.getCode())) {
            status = HttpStatus.FORBIDDEN;
            detail = exception.getMessage();
        } else {
            /* 新错误必须显式登记 HTTP 语义，未知业务错误先按服务故障处理。 */
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = 500000;
            detail = "内部服务异常";
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty("code", code);
        return ResponseEntity.status(status).body(problem);
    }
}
