package com.nexusauth.controller;

import com.nexusauth.api.system.SystemPermissionApi;
import com.nexusauth.api.system.dto.UserPermissionInfo;
import com.nexusauth.service.PermissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部权限查询入口，数据库校验与授权查询由 System Service 完成
 *
 * @author niuren
 * @date 2026-10-08 21:53
 */
@RestController
public class InternalPermissionController implements SystemPermissionApi {

    private final PermissionService permissionService;

    public InternalPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public UserPermissionInfo getCurrentUserPermissionInfo() {
        return permissionService.getCurrentUserPermissionInfo();
    }
}
