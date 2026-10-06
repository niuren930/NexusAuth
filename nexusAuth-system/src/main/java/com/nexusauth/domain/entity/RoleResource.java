package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nexusauth.domain.BaseEntity;
import lombok.Data;

/**
 * 租户角色与应用资源的授权关系
 *
 * @author niuren
 * @date 2026-10-06 20:55
 */
@Data
@TableName("na_role_resource")
public class RoleResource extends BaseEntity {

    /** 授权关系 ID，由 MyBatis-Plus ASSIGN_ID 生成。 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 角色所属租户，由经过校验的当前租户上下文填充。 */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /** 租户角色 ID，对应 na_role.id。 */
    private Long roleId;

    /** 应用资源 ID，对应 na_resource.id。 */
    private Long resourceId;
}
