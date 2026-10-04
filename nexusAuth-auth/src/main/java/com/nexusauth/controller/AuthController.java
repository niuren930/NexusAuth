package com.nexusauth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 基础认证接口。
 *
 * @author niuren
 * @date 2026-10-04 08:21
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登录。
     */
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    /**
     * 当前登录信息。
     */
    @GetMapping("/me")
    public Map<String, Object> me() {

        StpUtil.checkLogin();

        return Map.of(
                "userId",
                StpUtil.getLoginIdAsLong()
        );
    }

    /**
     * 退出登录。
     */
    @PostMapping("/logout")
    public void logout() {

        StpUtil.checkLogin();

        authService.logout();
    }
}