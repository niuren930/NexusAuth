package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nexusauth.domain.BaseEntity;
import com.nexusauth.domain.enums.RoleStatus;
import lombok.Data;

/**
 * 租户角色实体
 * <p>
 * 角色属于租户
 *
 * @author niuren
 * @date 2026-10-06 14:19
 */
@Data
@TableName("na_role")
public class Role extends BaseEntity {
    /**
     * 角色 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属租户 ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 所属应用 ID
     * 0表示租户级公共角色，非0表示指定应用角色
     */
    private Long appId;

    /**
     * 角色编码；在同一租户、应用及未删除范围内唯一
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 角色状态；禁用角色不参与权限计算。
     */
    private RoleStatus status;

    /**
     * 角色备注。
     */
    private String remark;

    /**
     * 逻辑删除标记，0 表示未删除，非 0 表示删除时的 Unix 秒级时间戳。
     */
    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    private Long deletedAt;
}
