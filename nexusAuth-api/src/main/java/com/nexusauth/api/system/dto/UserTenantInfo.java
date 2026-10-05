package com.nexusauth.api.system.dto;

/**
 * 用户可访问租户信息
 *
 * system → auth 内部传输使用
 *
 * @author niuren
 * @date 2026-10-05 10:53
 */
public record UserTenantInfo(
        // 成员id
        Long memberId,
        // 租户id
        Long tenantId,
        // 租户编码code
        String tenantCode,
        // 租户名称
        String tenantName,
        // 成员名称
        String memberName,
        // 是否租户所有者
        Boolean owner,
        // ？
        String logo
) {
}
