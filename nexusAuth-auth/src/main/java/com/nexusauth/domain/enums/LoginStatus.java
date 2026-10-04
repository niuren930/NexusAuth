package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 登录结果。
 */
public enum LoginStatus {

    FAILURE(0, "失败"),

    SUCCESS(1, "成功");

    @EnumValue
    private final Integer code;

    private final String description;

    LoginStatus(
            Integer code,
            String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}