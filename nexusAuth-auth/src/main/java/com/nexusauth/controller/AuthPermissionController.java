package com.nexusauth.controller;

import com.nexusauth.api.system.dto.UserPermissionInfo;
import com.nexusauth.core.Result;
import com.nexusauth.service.AuthPermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前登录用户在当前租户的授权事实查询
 *
 * @author niuren
 * @date 2026-10-08 22:30
 */
@RestController
@RequestMapping("/auth")
public class AuthPermissionController {
    private final AuthPermissionService authPermissionService;

    public AuthPermissionController(AuthPermissionService authPermissionService) {
        this.authPermissionService = authPermissionService;
    }

    /**
     * 权限数据保持应用归属；前端展示不能替代后端鉴权。
     */
    @GetMapping("/permissions")
    public Result<UserPermissionInfo> permissions() {
        return Result.success(authPermissionService.getCurrentUserPermissionInfo());
    }
}
