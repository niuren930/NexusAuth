package com.nexusauth.service;

import com.nexusauth.domain.dto.LoginClientInfo;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.domain.vo.TenantVO;

import java.util.List;

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

    /**
     * 查询我的租户，即当前登陆用户的租户
     */
    List<TenantVO> getMyTenants();

    /**
     * 切换租户
     * @param tenantId 租户id
     */
    TenantVO switchTenant(Long tenantId);
}
