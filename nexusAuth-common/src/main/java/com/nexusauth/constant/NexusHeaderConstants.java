package com.nexusauth.constant;

/**
 * 内部服务上下文请求头常量
 *
 * 这些请求头用于 NexusAuth 微服务之间传递已经验证过的用户身份和租户上下文。
 *
 * 注意：
 *      这些 Header 不是面向公网客户端设计的身份认证凭证，
 *      不能直接信任客户端自行传入的值。
 *
 * 待接入 Gateway 后，Gateway 需要负责清理外部传入的 X-Nexus-* Header，并根据真实认证结果重新生成内部上下文。
 *
 * @author niuren
 * @date 2026-10-05 18:25
 */
public class NexusHeaderConstants {
    /**
     * 当前登录用户ID。
     */
    public static final String USER_ID = "X-Nexus-User-Id";

    /**
     * 当前租户ID。
     *
     * 用户登录后尚未选择租户时，该 Header 不存在。
     */
    public static final String TENANT_ID = "X-Nexus-Tenant-Id";

    private NexusHeaderConstants() {
    }
}
