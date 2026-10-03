package com.nexusauth.context;

import lombok.val;

/**
 * 当前线程租户上下文。
 *
 * 用于保存一次请求生命周期中的当前租户ID。
 *
 * 注意：
 * 使用 ThreadLocal 后必须在请求结束时调用 clear()，
 * 防止线程池复用导致租户上下文串线。
 *
 * @author niuren
 * @date 2026-10-03 14:30
 */
public class TenantContextHolder {

    private static final ThreadLocal<Long> TENANT_CONTEXT = new ThreadLocal<>();

    private TenantContextHolder(){}

    /**
     * 设置当前租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_CONTEXT.set(tenantId);
    }

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        return TENANT_CONTEXT.get();
    }

    /**
     * 获取当前租户ID
     *
     * 不存在租户上下文时直接抛出异常，
     * 适用于必须处于租户环境中的业务
     */
    public static Long requireTenantId() {
        Long tenantId = getTenantId();

        if (tenantId == null) {
            throw new IllegalStateException("当前租户信息缺失");
        }

        return tenantId;
    }

    /**
     * 清理当前线程中的租户上下午
     *
     * ThreadLocal 必须在线程池环境中主动清理
     */
    public static void clear() {
        TENANT_CONTEXT.remove();
    }

}
