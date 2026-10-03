package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 租户状态。
 *
 * @author niuren
 */
@Getter
public enum TenantStatus {
    NORMAL(1, "正常"),

    DISABLED(2, "禁用"),

    EXPIRED(3, "过期"),

    CLOSED(4, "关闭");

    /**
     * 实际存入数据库的值。
     */
    @EnumValue
    private final Integer code;

    private final String description;

    TenantStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
