package com.nexusauth.api.system.dto;

/**
 * 当前租户成员通过角色获得的正常资源权限
 *
 * @param resourceId     资源 ID
 * @param appId          资源所属应用
 * @param permissionCode 非空权限码
 * @author niuren
 * @date 2026-10-06 21:49
 */
public record PermissionGrantInfo(Long resourceId, Long appId, String permissionCode) {
}
