package com.nexusauth.domain.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 切换租户
 *
 * @author niuren
 * @date 2026-10-05 12:01
 */
public record SwitchTenantRequest(
        @NotNull(message = "租户ID不能为空")
        Long tenantId

) {
}
