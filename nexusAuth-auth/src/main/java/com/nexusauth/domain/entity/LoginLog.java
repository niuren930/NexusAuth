package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexusauth.domain.enums.LoginStatus;
import com.nexusauth.domain.enums.LoginType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录日志
 *
 * @author niuren
 * @date 2026-10-04 10:15
 */
@Data
@TableName("na_login_log")
public class LoginLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 登录成功后对应的用户ID。
     * <p>
     * 用户不存在时可以为空。
     */
    private Long userId;

    /**
     * 当前租户。
     * <p>
     * 基础登录阶段尚未选择租户，因此允许为空。
     */
    private Long tenantId;

    /**
     * 登录来源应用。
     * <p>
     * 当前阶段允许为空。
     */
    private Long appId;

    /**
     * 登录时提交的用户名。
     */
    private String username;

    /**
     * 登录方式。
     */
    private LoginType loginType;

    /**
     * 登录结果。
     */
    private LoginStatus loginStatus;

    /**
     * 客户端IP。
     */
    private String ip;

    /**
     * User-Agent。
     */
    private String userAgent;

    /**
     * 失败原因。
     * <p>
     * 成功登录时为空。
     */
    private String failureReason;

    /**
     * 登录时间。
     */
    private LocalDateTime loginTime;
}
