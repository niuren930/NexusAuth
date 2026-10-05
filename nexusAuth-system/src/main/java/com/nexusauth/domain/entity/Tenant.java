package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexusauth.domain.BaseEntity;
import com.nexusauth.domain.enums.TenantStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户表
 *
 * @author niuren
 * @date 2026-10-03 16:50
 */
@TableName("na_tenant")
@Data
public class Tenant extends BaseEntity {

    /**
     * 租户ID。
     *
     * 使用 MyBatis-Plus ASSIGN_ID 自动生成分布式ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户唯一编码，例如 nexus、company-a。
     */
    private String tenantCode;

    /**
     * 租户名称。
     */
    private String tenantName;

    /**
     * 租户所有者用户ID，对应 na_user.id。
     */
    private Long ownerUserId;

    /**
     * 租户 Logo URL。
     */
    private String logo;

    /**
     * 租户状态：
     * 1 - 正常
     * 2 - 禁用
     * 3 - 过期
     * 4 - 关闭
     */
    private TenantStatus status;

    /**
     * 租户过期时间。
     *
     * NULL 表示暂不过期。
     */
    private LocalDateTime expireTime;

    /**
     * 最大成员数量。
     *
     * NULL 表示暂不限制。
     */
    private Integer maxUsers;

    /**
     * 租户备注。
     */
    private String remark;

}
