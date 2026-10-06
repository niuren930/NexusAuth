package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexusauth.domain.BaseEntity;
import com.nexusauth.domain.enums.ResourceStatus;
import com.nexusauth.domain.enums.ResourceType;
import lombok.Data;

/**
 * 应用权限资源实体
 *
 * @author niuren
 * @date 2026-10-06 20:51
 */
@Data
@TableName("na_resource")
public class Resource extends BaseEntity {

    /**
     * 资源 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属应用 ID，对应 na_app.id。
     * 应用关联策略尚待确认，不在资源模型中约定 0 为默认应用。
     */
    private Long appId;

    /**
     * 父资源 ID，0 表示根节点；后续 Service 校验同应用及无循环。
     */
    private Long parentId;

    /**
     * 资源名称。
     */
    private String resourceName;

    /**
     * 资源类型：目录、菜单、按钮或 API。
     */
    private ResourceType resourceType;

    /**
     * 权限码，沿用 app:module:action 格式。
     * 目录允许为空；空值不应被解释为通配权限。
     */
    private String permissionCode;

    /**
     * 前端路由路径，供菜单资源使用。
     */
    private String routePath;

    /**
     * 后端接口路径，供 API 资源使用。
     */
    private String apiPath;

    /**
     * HTTP 方法，例如 GET、POST、PUT、DELETE。
     */
    private String httpMethod;

    /**
     * 菜单图标标识。
     */
    private String icon;

    /**
     * 排序值，越小越靠前。
     */
    private Integer sort;

    /**
     * 资源状态；禁用资源不参与有效权限计算。
     */
    private ResourceStatus status;

    /**
     * 逻辑删除标记，0 表示未删除，非 0 为 Unix 秒级删除时间戳。
     * 自定义权限 SQL 必须显式排除已删除资源。
     */
    @TableLogic(value = "0", delval = "UNIX_TIMESTAMP()")
    private Long deletedAt;
}
