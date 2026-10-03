package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 租户成员状态。
 *
 * @author niuren
 */
@Getter
public enum TenantMemberStatus {

    NORMAL(1, "正常"),

    DISABLED(2, "禁用"),

    EXITED(3, "已退出");

    @EnumValue
    private final Integer code;

    private final String description;

    TenantMemberStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}