package com.nexusauth.api.system.dto;

/**
 * 用户认证信息
 *
 * 仅供 NexusAuth 内部认证服务使用，
 * 不允许直接返回给前端
 *
 * 题外话：
 *      这里之所以用 record，是因为这里是 纯数据传输对象，无需setUserId()、setUsername()
 *      Java 会自动生成：userInfo.userId()、userInfo.username()，非常适合微服务 DTO。
 *
 * @author niuren
 * @date 2026-10-03 21:51
 */
public record UserAuthInfo(
        // 用户ID
        Long userId,
        // 登录用户名
        String username,
        // 密码哈希
        String passwordHash,
        // 用户状态
        Integer status
) {
}
