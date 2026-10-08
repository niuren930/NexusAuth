package com.nexusauth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.api.system.dto.UserPermissionInfo;
import com.nexusauth.client.SystemPermissionClient;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.nexusauth.exception.AuthErrorCode;
import com.nexusauth.exception.BusinessException;
import com.nexusauth.exception.PermissionAccessDeniedException;
import com.nexusauth.exception.PermissionServiceUnavailableException;
import com.nexusauth.service.AuthPermissionService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 当前 Token 的租户权限查询与远程失败转换
 * <p>
 * Auth 校验登录态，System 校验数据库事实；服务调用失败必须显式失败
 *
 * @author niuren
 * @date 2026-10-08 22:20
 */
@Slf4j
@Service
public class AuthPermissionServiceImpl implements AuthPermissionService {

    private final SystemPermissionClient systemPermissionClient;

    public AuthPermissionServiceImpl(SystemPermissionClient systemPermissionClient) {
        this.systemPermissionClient = systemPermissionClient;
    }

    @Override
    public UserPermissionInfo getCurrentUserPermissionInfo() {

        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        Long tenantId = TenantContextHolder.getTenantId();

        if (tenantId == null) {
            throw new BusinessException(AuthErrorCode.TENANT_NOT_SELECTED);
        }

        if (userId <= 0 || tenantId <= 0 || !userId.equals(OperatorContextHolder.getUserId())) {
            log.error("权限查询的本地身份上下文不一致");
            throw new PermissionServiceUnavailableException();
        }

        UserPermissionInfo info;
        try {
            info = systemPermissionClient.getCurrentUserPermissionInfo();
        } catch (FeignException.Forbidden exception) {
            /* System 已确认数据库中的用户/成员/租户不可访问。 */
            throw new PermissionAccessDeniedException();
        } catch (FeignException exception) {
            /*
             * 服务身份 401、接口缺失 404、内部上下文 400、超时和 5xx 都是上游失败。
             * 不打印异常、请求头或响应体；更不能返回空列表掩盖服务故障。
             */
            log.warn("System 权限查询失败，HTTP 状态={}", exception.status());
            throw new PermissionServiceUnavailableException();
        }

        if (!hasValidPayload(info, userId, tenantId)) {
            log.error("System 权限查询响应不符合当前上下文契约");
            throw new PermissionServiceUnavailableException();
        }
        return info;
    }

    /**
     * 校验身份关联与基本数据形状，不在此推导应用开通、资源分配或鉴权策略。
     * 成员 ID 由 System 重新查询，不与 Token-Session 的旧成员 ID 强行绑定。
     */
    private boolean hasValidPayload(UserPermissionInfo info, Long userId, Long tenantId) {
        if (info == null || !userId.equals(info.userId()) || !tenantId.equals(info.tenantId())
                || info.memberId() == null || info.memberId() <= 0
                || info.roleGrants() == null || info.permissionGrants() == null) {
            return false;
        }

        boolean validRoles = info.roleGrants().stream().allMatch(grant ->
                grant != null && grant.roleId() != null && grant.roleId() > 0
                        && grant.appId() != null && grant.appId() >= 0
                        && StringUtils.hasText(grant.roleCode())
        );
        boolean validPermissions = info.permissionGrants().stream().allMatch(grant ->
                grant != null && grant.resourceId() != null && grant.resourceId() > 0
                        && grant.appId() != null && grant.appId() > 0
                        && StringUtils.hasText(grant.permissionCode())
        );
        return validRoles && validPermissions;
    }
}
