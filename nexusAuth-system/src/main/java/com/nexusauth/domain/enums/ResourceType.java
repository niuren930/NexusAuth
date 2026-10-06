package com.nexusauth.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 应用权限资源类型
 *
 * @author niuren
 * @date 2026-10-06 20:49
 */
@Getter
public enum ResourceType {
    /**
     * 目录节点，用于组织资源层级，权限码允许为空。
     */
    DIRECTORY("DIRECTORY", "目录"),

    /**
     * 菜单资源，保存前端路由等展示信息。
     */
    MENU("MENU", "菜单"),

    /**
     * 按钮资源，表示页面中的操作入口。
     */
    BUTTON("BUTTON", "按钮"),

    /**
     * API 资源，描述 HTTP 方法与接口路径。
     */
    API("API", "接口");

    /**
     * 持久化字符串，与 na_resource.resource_type 保持一致。
     */
    @EnumValue
    private final String code;

    /**
     * 类型的业务说明。
     */
    private final String description;

    ResourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
