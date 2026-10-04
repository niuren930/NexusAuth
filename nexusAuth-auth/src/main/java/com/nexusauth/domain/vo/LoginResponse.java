package com.nexusauth.domain.vo;

/**
 * 登录响应
 *
 * @param userId    用户ID
 * @param username  用户名
 * @param tokenName Token参数名称
 * @param tokenValue Token值
 *
 * @author niuren
 * @date 2026-10-04 07:58
 */
public record LoginResponse(
        Long userId,
        String username,
        String tokenName,
        String tokenValue
) {
}
