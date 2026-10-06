package com.nexusauth.api.system.dto;

/**
 * 当前租户成员拥有的正常角色
 *
 * @param roleId   角色 ID
 * @param appId    角色所属应用，0 表示租户级公共角色
 * @param roleCode 角色编码
 * @author niuren
 * @date 2026-10-06 21:48
 */
public record RoleGrantInfo(Long roleId, Long appId, String roleCode) {
}
