package com.nexusauth.api.system;

import com.nexusauth.api.system.dto.UserAuthInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * System 服务用户内部接口契约。
 * <p>
 * 供其他 NexusAuth 内部服务调用，
 * 不属于前端公开接口。
 *
 * @author niuren
 */
public interface SystemUserApi {

    /**
     * 根据用户名获取认证所需用户信息。
     *
     * @param username 用户名
     * @return 用户认证信息
     */
    @GetMapping("/internal/users/auth-info")
    UserAuthInfo getUserAuthInfo(@RequestParam("username") String username);

}
