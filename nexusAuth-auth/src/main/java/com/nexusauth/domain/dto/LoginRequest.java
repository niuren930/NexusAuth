package com.nexusauth.domain.dto;

/**
 * 用户登录请求
 *
 * @author niuren
 * @date 2026-10-04 07:56
 */
public record LoginRequest(
        // 用户名
        String username,
        // 原始密码
        String password
) {
}
