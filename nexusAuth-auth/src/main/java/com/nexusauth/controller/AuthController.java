package com.nexusauth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.core.Result;
import com.nexusauth.domain.dto.LoginClientInfo;
import com.nexusauth.domain.dto.LoginRequest;
import com.nexusauth.domain.vo.LoginResponse;
import com.nexusauth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {

        LoginClientInfo clientInfo = new LoginClientInfo(
                servletRequest.getRemoteAddr(),
                servletRequest.getHeader("User-Agent")
        );

        return Result.success(authService.login(request, clientInfo));
    }

    /**
     * 当前登录信息。
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me() {

        StpUtil.checkLogin();

        return Result.success(
                Map.of(
                        "userId",
                        StpUtil.getLoginIdAsLong()
                ));
    }

    /**
     * 退出登录。
     */
    @PostMapping("/logout")
    public Result<Void> logout() {

        StpUtil.checkLogin();

        authService.logout();

        return Result.success();
    }
}