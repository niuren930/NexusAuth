package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 用户状态。
 *
 * @author niuren
 */
@Getter
public enum UserStatus {

    /**
     * 正常。
     */
    NORMAL(1, "正常"),

    /**
     * 禁用。
     */
    DISABLED(2, "禁用"),

    /**
     * 锁定。
     */
    LOCKED(3, "锁定"),

    /**
     * 已注销。
     */
    CLOSED(4, "注销");

    /**
     * 实际存入数据库的值。
     */
    @EnumValue
    private final Integer code;

    /**
     * 状态描述。
     */
    private final String description;

    UserStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}