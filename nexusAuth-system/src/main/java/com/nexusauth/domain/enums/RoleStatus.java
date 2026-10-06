package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 租户角色状态
 *
 * @author niuren
 * @date 2026-10-06 14:17
 */
@Getter
public enum RoleStatus {
    /**
     * 正常角色，可以参与授权。
     */
    NORMAL(1, "正常"),

    /**
     * 禁用角色，不应贡献角色码或权限码。
     */
    DISABLED(2, "禁用");

    /**
     * 实际持久化值，与 na_role.status 的数值定义保持一致。
     */
    @EnumValue
    private final Integer code;

    /**
     * 状态说明，用于解释业务含义。
     */
    private final String description;

    RoleStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
