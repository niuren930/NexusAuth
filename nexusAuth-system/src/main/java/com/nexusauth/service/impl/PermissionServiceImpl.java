package com.nexusauth.service.impl;

import com.nexusauth.api.system.dto.PermissionGrantInfo;
import com.nexusauth.api.system.dto.RoleGrantInfo;
import com.nexusauth.api.system.dto.UserPermissionInfo;
import com.nexusauth.api.system.dto.UserTenantInfo;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.nexusauth.domain.entity.User;
import com.nexusauth.domain.enums.ResourceStatus;
import com.nexusauth.domain.enums.RoleStatus;
import com.nexusauth.domain.enums.UserStatus;
import com.nexusauth.exception.BusinessException;
import com.nexusauth.exception.SystemErrorCode;
import com.nexusauth.mapper.PermissionMapper;
import com.nexusauth.mapper.UserMapper;
import com.nexusauth.service.PermissionService;
import com.nexusauth.service.TenantAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 当前租户授权事实查询实现
 *
 * @author niuren
 * @date 2026-10-06 21:59
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final UserMapper userMapper;
    private final TenantAccessService tenantAccessService;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(UserMapper userMapper, TenantAccessService tenantAccessService, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.tenantAccessService = tenantAccessService;
        this.permissionMapper = permissionMapper;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public UserPermissionInfo getCurrentUserPermissionInfo() {

        Long userId = OperatorContextHolder.getUserId();
        Long tenantId = TenantContextHolder.getTenantId();

        if (userId == null || userId <= 0 || tenantId == null || tenantId <= 0) {
            throw new BusinessException(SystemErrorCode.PERMISSION_CONTEXT_INVALID);
        }

        // 已有账号不代表此刻仍允许访问，因此查询查询权限状态
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != UserStatus.NORMAL) {
            throw new BusinessException(SystemErrorCode.TENANT_ACCESS_DENIED);
        }

        // 同上再次校验此刻用户和租户关系
        UserTenantInfo accessInfo = tenantAccessService.getCurrentTenantAccessInfo();
        if (accessInfo == null) {
            throw new BusinessException(SystemErrorCode.TENANT_ACCESS_DENIED);
        }

        Long memberId = accessInfo.memberId();
        // 查询有效角色
        List<RoleGrantInfo> roleGrantInfos = permissionMapper.selectRoleGrants(memberId, tenantId, RoleStatus.NORMAL.getCode());
        // 查询有效资源权限
        List<PermissionGrantInfo> permissionGrantInfos = permissionMapper.selectPermissionGrants(memberId, tenantId, RoleStatus.NORMAL.getCode(), ResourceStatus.NORMAL.getCode());

        return new UserPermissionInfo(userId, tenantId, memberId, roleGrantInfos, permissionGrantInfos);
    }
}
