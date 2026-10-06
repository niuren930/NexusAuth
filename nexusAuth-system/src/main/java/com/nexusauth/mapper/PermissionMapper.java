package com.nexusauth.mapper;

import com.nexusauth.api.system.dto.PermissionGrantInfo;
import com.nexusauth.api.system.dto.RoleGrantInfo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * 当前租户成员授权
 * <p>
 * 只承载查询，不对应单一持久化实体，因此不继承 BaseMapper
 *
 * @author niuren
 * @date 2026-10-06 21:52
 */
public interface PermissionMapper {
    /**
     * 查询有效角色；
     * memberId 必须由当前用户的有效成员关系取得。
     */
    List<RoleGrantInfo> selectRoleGrants(
            @Param("memberId") Long memberId,
            @Param("tenantId") Long tenantId,
            @Param("roleStatus") Integer roleStatus
    );

    /**
     * 查询有效资源权限，
     * 正常状态值由 Service 从对应枚举传入。
     */
    List<PermissionGrantInfo> selectPermissionGrants(
            @Param("memberId") Long memberId,
            @Param("tenantId") Long tenantId,
            @Param("roleStatus") Integer roleStatus,
            @Param("resourceStatus") Integer resourceStatus
    );
}
