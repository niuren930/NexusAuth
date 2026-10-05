package com.nexusauth.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.api.system.dto.UserAuthInfo;
import com.nexusauth.api.system.dto.UserTenantInfo;
import com.nexusauth.client.SystemTenantClient;
import com.nexusauth.client.SystemUserClient;
import com.nexusauth.constant.AuthSessionConstants;
import com.nexusauth.domain.dto.LoginClientInfo;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.domain.vo.TenantVO;
import com.nexusauth.exception.AuthErrorCode;
import com.nexusauth.exception.BusinessException;
import com.nexusauth.service.AuthService;
import com.nexusauth.service.LoginLogService;
import feign.FeignException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现
 *
 * @author niuren
 * @date 2026-10-04 08:11
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final SystemUserClient systemUserClient;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginLogService loginLogService;
    private final SystemTenantClient systemTenantClient;

    public AuthServiceImpl(SystemUserClient systemUserClient, BCryptPasswordEncoder passwordEncoder, LoginLogService loginLogService, SystemTenantClient systemTenantClient) {
        this.systemUserClient = systemUserClient;
        this.passwordEncoder = passwordEncoder;
        this.loginLogService = loginLogService;
        this.systemTenantClient = systemTenantClient;
    }

    @Override
    public LoginResponse login(LoginRequest request,
                               LoginClientInfo clientInfo) {

        // 1. 查询用户认证信息
        UserAuthInfo userAuthInfo;
        try {
            userAuthInfo = systemUserClient.getUserAuthInfo(request.username());
        } catch (FeignException.NotFound exception) {

            loginLogService.recordFailure(
                    null,
                    request.username(),
                    "USER_NOT_FOUND",
                    clientInfo
            );

            // 不告诉外部“用户名不存在”
            throw new BusinessException(
                    AuthErrorCode.USERNAME_OR_PASSWORD_ERROR
            );

        } catch (FeignException exception) {
            // system 服务不可用、超时、500 等
            throw new BusinessException(
                    AuthErrorCode.USER_SERVICE_UNAVAILABLE
            );
        }


        // 2. 校验密码
        boolean matches = passwordEncoder.matches(request.password(), userAuthInfo.passwordHash());
        if (!matches) {

            loginLogService.recordFailure(
                    userAuthInfo.userId(),
                    userAuthInfo.username(),
                    "PASSWORD_ERROR",
                    clientInfo
            );

            throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 检查账号是否允许登录
        if (!userAuthInfo.loginAllowed()) {

            loginLogService.recordFailure(
                    userAuthInfo.userId(),
                    userAuthInfo.username(),
                    "ACCOUNT_NOT_ALLOWED",
                    clientInfo
            );

            throw new BusinessException(AuthErrorCode.ACCOUNT_NOT_ALLOWED);
        }

        // 4. sa-token 创建登录态
        StpUtil.login(userAuthInfo.userId());

        loginLogService.recordSuccess(
                userAuthInfo.userId(),
                userAuthInfo.username(),
                clientInfo
        );

        // 5. 返回登录结果
        return new LoginResponse(
                userAuthInfo.userId(),
                userAuthInfo.username(),
                StpUtil.getTokenName(),
                StpUtil.getTokenValue()
        );
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public List<TenantVO> getMyTenants() {

        StpUtil.checkLogin();

        long userId = StpUtil.getLoginIdAsLong();

        return systemTenantClient.getUserTenants(userId).stream()
                .map(item -> new TenantVO(item.tenantId(),
                        item.tenantCode(),
                        item.tenantName(),
                        item.memberName(),
                        item.owner(),
                        item.logo())
                )
                .toList();
    }

    @Override
    public TenantVO switchTenant(Long tenantId) {

        StpUtil.checkLogin();

        long userId = StpUtil.getLoginIdAsLong();

        // 验证成员和租户关系
        UserTenantInfo tenantAccessInfo = systemTenantClient.getTenantAccessInfo(userId, tenantId);

        if (tenantAccessInfo == null) {
            throw new BusinessException(AuthErrorCode.TENANT_ACCESS_DENIED);
        }

        SaSession tokenSession = StpUtil.getTokenSession();
        tokenSession.set(AuthSessionConstants.TENANT_ID,tenantAccessInfo.tenantId());
        tokenSession.set(AuthSessionConstants.MEMBER_ID,tenantAccessInfo.memberId());

        return new TenantVO(
                tenantAccessInfo.tenantId(),
                tenantAccessInfo.tenantCode(),
                tenantAccessInfo.tenantName(),
                tenantAccessInfo.memberName(),
                tenantAccessInfo.owner(),
                tenantAccessInfo.logo()
        );
    }
}
