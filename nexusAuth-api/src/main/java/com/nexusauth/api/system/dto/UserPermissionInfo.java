package com.nexusauth.api.system.dto;

import java.util.List;

/**
 * 当前用户在当前租户的 RBAC 授权信息
 *
 * @param userId           全局用户 ID
 * @param tenantId         当前租户 ID
 * @param memberId         当前用户在该租户中的成员 ID
 * @param roleGrants       正常、未删除的角色授权
 * @param permissionGrants 经有效角色关系获得的正常、未删除资源权限
 * @author niuren
 * @date 2026-10-06 21:50
 */
public record UserPermissionInfo(
        Long userId,
        Long tenantId,
        Long memberId,
        List<RoleGrantInfo> roleGrants,
        List<PermissionGrantInfo> permissionGrants
) {
    /**
     * 保留不可变集合；
     * 不接受 null 集合或 null 元素冒充正常空授权。
     */
    public UserPermissionInfo {
        roleGrants = List.copyOf(roleGrants);
        permissionGrants = List.copyOf(permissionGrants);
    }
}
