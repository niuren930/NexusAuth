package com.nexusauth.domain.vo;

/**
 * auth 对外暴露的 租户字段
 *
 * @author niuren
 * @date 2026-10-05 11:53
 */
public record TenantVO(
        Long tenantId,
        String tenantCode,
        String tenantName,
        String memberName,
        Boolean owner,
        String logo
) {
}
