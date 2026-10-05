package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nexusauth.domain.BaseEntity;
import com.nexusauth.domain.enums.TenantMemberStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户成员关系表
 *
 * 一个全局用户可加入多个租户
 * 一个租户也可以包含多个用户。
 *
 * @author niuren
 * @date 2026-10-03 16:50
 */
@TableName("na_tenant_member")
@Data
public class TenantMember extends BaseEntity {

    /**
     * 成员关系ID。
     *
     * 使用 MyBatis-Plus ASSIGN_ID 自动生成分布式ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户ID，对应 na_tenant.id。
     *
     * 关键点：MyBatis-Plus 的 TenantLineInnerInterceptor 不只是查数据时拼 tenant_id，它也会处理插入；
     * 官方文档明确提醒，租户字段通常要配合自动填充，否则插入时可能没有正确保存，因此 添加@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 用户ID，对应 na_user.id。
     */
    private Long userId;

    /**
     * 用户在当前租户中的显示名称。
     *
     * 为空时使用用户昵称。
     */
    private String memberName;

    /**
     * 是否租户所有者：
     * 0 - 否
     * 1 - 是
     */
    private Boolean isOwner;

    /**
     * 成员状态：
     * 1 - 正常
     * 2 - 禁用
     * 3 - 已退出
     */
    private TenantMemberStatus status;

    /**
     * 加入租户时间。
     */
    private LocalDateTime joinedAt;

}
