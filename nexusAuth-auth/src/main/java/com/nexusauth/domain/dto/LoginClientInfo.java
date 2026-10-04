package com.nexusauth.domain.dto;

/**
 * 登录客户端信息
 *
 * @param ip        客户端IP
 * @param userAgent User-Agent
 *
 * @author niuren
 * @date 2026-10-04 10:20
 */
public record LoginClientInfo(
        String ip,
        String userAgent
) {
}
