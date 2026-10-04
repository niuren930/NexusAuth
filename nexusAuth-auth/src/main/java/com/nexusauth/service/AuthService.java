package com.nexusauth.service;

import com.nexusauth.domain.dto.LoginClientInfo;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;

/**
 * 认证服务。
 */
public interface AuthService {
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request,
                        LoginClientInfo clientInfo);

    /**
     * 用户退出
     */
    void logout();
}
