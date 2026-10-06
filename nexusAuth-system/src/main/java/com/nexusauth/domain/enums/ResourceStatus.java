package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 应用权限资源状态
 *
 * @author niuren
 * @date 2026-10-06 20:50
 */
@Getter
public enum ResourceStatus {
    /** 正常资源，可以参与授权计算。 */
    NORMAL(1, "正常"),

    /** 禁用资源，不应贡献有效权限码。 */
    DISABLED(2, "禁用");

    /** 持久化值*/
    @EnumValue
    private final Integer code;

    private final String description;

    ResourceStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
