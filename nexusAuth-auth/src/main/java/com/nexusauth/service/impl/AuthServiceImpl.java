package com.nexusauth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.api.system.dto.UserAuthInfo;
import com.nexusauth.client.SystemUserClient;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.exception.AuthErrorCode;
import com.nexusauth.exception.BusinessException;
import com.nexusauth.service.AuthService;
import feign.FeignException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthServiceImpl(SystemUserClient systemUserClient, BCryptPasswordEncoder passwordEncoder) {
        this.systemUserClient = systemUserClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. 查询用户认证信息
        UserAuthInfo userAuthInfo;
        try {
            userAuthInfo = systemUserClient.getUserAuthInfo(request.username());
        } catch (FeignException.NotFound exception) {
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
            throw new BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 检查账号是否允许登录
        if (!userAuthInfo.loginAllowed()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_NOT_ALLOWED);
        }

        // 4. sa-token 创建登录态
        StpUtil.login(userAuthInfo.userId());

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
}
