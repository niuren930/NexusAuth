package com.nexusauth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.api.system.dto.UserAuthInfo;
import com.nexusauth.client.SystemUserClient;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.service.AuthService;
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
        UserAuthInfo userAuthInfo = systemUserClient.getUserAuthInfo(request.username());

        // 2. 检查账号是否允许登录
        if (!userAuthInfo.loginAllowed()) {
            throw new IllegalStateException("当前账号不允许登录");
        }

        // 3. 校验密码
        boolean matches = passwordEncoder.matches(request.password(), userAuthInfo.passwordHash());
        if (!matches) {
            throw new IllegalStateException("账号密码错误");
        }

        // 4. sa-token 创建登录态
        StpUtil.login(userAuthInfo.userId());

        // 5. 获取本次token
        String tokenName = StpUtil.getTokenName();
        String tokenValue = StpUtil.getTokenValue();

        // 6. 返回登录结果
        return new LoginResponse(
                userAuthInfo.userId(),
                userAuthInfo.username(),
                tokenName,
                tokenValue
        );
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
