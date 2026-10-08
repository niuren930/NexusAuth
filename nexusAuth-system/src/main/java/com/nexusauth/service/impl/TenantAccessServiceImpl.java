package com.nexusauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nexusauth.api.system.dto.UserTenantInfo;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.nexusauth.domain.entity.Tenant;
import com.nexusauth.domain.entity.TenantMember;
import com.nexusauth.domain.enums.TenantMemberStatus;
import com.nexusauth.domain.enums.TenantStatus;
import com.nexusauth.exception.BusinessException;
import com.nexusauth.exception.SystemErrorCode;
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

        requireCurrentUser(userId);

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

    /**
     * 跨租户查询仅供“我的租户”与“我是否能进入该租户”。
     *
     * @param requestedUserId
     */
    private void requireCurrentUser(Long requestedUserId) {
        Long operatorId = OperatorContextHolder.getUserId();
        if (operatorId == null || operatorId <= 0) {
            throw new BusinessException(SystemErrorCode.OPERATOR_CONTEXT_INVALID);
        }
        if (requestedUserId == null || !operatorId.equals(requestedUserId)) {
            throw new BusinessException(SystemErrorCode.CURRENT_USER_MISMATCH);
        }
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

    @Override
    public UserTenantInfo getCurrentTenantAccessInfo() {
        /*
         * 当前登录用户
         */
        Long userId = OperatorContextHolder.requireUserId();

        /*
         * 当前租户来自
         */
        Long tenantId = TenantContextHolder.requireTenantId();

        /*
         * 注意：
         * 这里故意只写 user_id 和成员状态，
         * 没有手动添加：
         *
         * .eq(TenantMember::getTenantId, tenantId)
         *
         * 因为我们就是要让 MyBatis-Plus
         * TenantLineInnerInterceptor 自动添加 tenant_id 条件。
         */
        TenantMember member =
                tenantMemberMapper.selectOne(
                        new LambdaQueryWrapper<TenantMember>()
                                .eq(TenantMember::getUserId, userId)
                                .eq(TenantMember::getStatus, TenantMemberStatus.NORMAL)
                );

        if (member == null) {
            return null;
        }

        /*
         * na_tenant 属于平台级表，
         * 在 NexusTenantLineHandler 中已经设置为忽略租户拦截。
         */
        Tenant tenant = tenantMapper.selectById(tenantId);

        if (tenant == null || tenant.getStatus() != TenantStatus.NORMAL) {
            return null;
        }

        return toUserTenantInfo(member, tenant);
    }
}
