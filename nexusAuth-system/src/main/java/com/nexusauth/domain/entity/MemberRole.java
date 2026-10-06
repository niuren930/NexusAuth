package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nexusauth.domain.BaseEntity;
import lombok.Data;

/**
 * 租户成员与角色的授权关系
 * <p>
 * 关系属于租户；新增授权前必须校验成员与角色属于同一当前租户
 *
 * @author niuren
 * @date 2026-10-06 20:54
 */
@Data
@TableName("na_member_role")
public class MemberRole extends BaseEntity {

    /** 授权关系 ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属租户，由已经校验的租户上下文填充。 */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /** 租户成员 ID，对应 na_tenant_member.id，不是全局 userId。 */
    private Long memberId;

    /** 租户角色 ID，对应 na_role.id。 */
    private Long roleId;
}
