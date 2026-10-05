package com.nexusauth.service.impl;

import com.nexusauth.api.system.dto.UserTenantInfo;
import com.nexusauth.domain.entity.Tenant;
import com.nexusauth.domain.entity.TenantMember;
import com.nexusauth.domain.enums.TenantMemberStatus;
import com.nexusauth.domain.enums.TenantStatus;
import com.nexusauth.mapper.TenantMapper;
import com.nexusauth.mapper.TenantMemberMapper;
import com.nexusauth.service.TenantAccessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 租户访问服务
 *
 * @author niuren
 * @date 2026-10-05 11:04
 */
@Service
public class TenantAccessServiceImpl implements TenantAccessService {

    private final TenantMemberMapper tenantMemberMapper;
    private final TenantMapper tenantMapper;

    public TenantAccessServiceImpl(TenantMemberMapper tenantMemberMapper, TenantMapper tenantMapper) {
        this.tenantMemberMapper = tenantMemberMapper;
        this.tenantMapper = tenantMapper;
    }

    @Override
    public List<UserTenantInfo> getUserTenants(Long userId) {

        // 跨租户查询到该用户可访问的所有租户
        List<TenantMember> tenantMemberList = tenantMemberMapper.selectByUserIdCrossTenant(userId);

        // 只保留关系正常的
        List<TenantMember> activeMembers = tenantMemberList.stream()
                .filter(tenantMember -> tenantMember.getStatus() == TenantMemberStatus.NORMAL)
                .toList();

        if (activeMembers.isEmpty())
            return List.of();

        List<Long> tenantIds = activeMembers.stream()
                .map(TenantMember::getTenantId)
                .distinct()
                .toList();

        List<Tenant> tenants = tenantMapper.selectByIds(tenantIds);
        Map<Long, Tenant> tenantMap = tenants.stream()
                .filter(tenant -> tenant.getStatus() == TenantStatus.NORMAL)
                .collect(Collectors.toMap(Tenant::getId, Function.identity()));

        return activeMembers.stream()
                .map(member -> {
                    Tenant tenant = tenantMap.get(member.getTenantId());
                    if (tenant == null) return null;
                    return toUserTenantInfo(member, tenant);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private UserTenantInfo toUserTenantInfo(TenantMember member, Tenant tenant) {
        return new UserTenantInfo(
                member.getId(),
                tenant.getId(),
                tenant.getTenantCode(),
                tenant.getTenantName(),
                member.getMemberName(),
                member.getIsOwner(),
                tenant.getLogo()
        );
    }

    @Override
    public UserTenantInfo getTenantAccessInfo(Long userId, Long tenantId) {
        return getUserTenants(userId)
                .stream()
                .filter(item ->
                        item.tenantId()
                                .equals(tenantId))
                .findFirst()
                .orElse(null);
    }
}
