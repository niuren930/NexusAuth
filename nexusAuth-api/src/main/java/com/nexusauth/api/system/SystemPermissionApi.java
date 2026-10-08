package com.nexusauth.api.system;

import com.nexusauth.api.system.dto.UserPermissionInfo;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * System 当前上下文权限查询的内部契约
 *
 * @author niuren
 * @date 2026-10-08 21:51
 */
public interface SystemPermissionApi {

    /**
     * 查询当前用户在当前租户的有效授权事实，保留应用归属。
     *
     * @return 有效成员及授权；没有授权时列表为空，业务失败不能返回空列表
     */
    @GetMapping("/internal/permissions/current")
    UserPermissionInfo getCurrentUserPermissionInfo();

}
