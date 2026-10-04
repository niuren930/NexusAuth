package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 登录方式。
 */
public enum LoginType {

    PASSWORD("PASSWORD", "账号密码");

    @EnumValue
    private final String code;

    private final String description;

    LoginType(
            String code,
            String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}