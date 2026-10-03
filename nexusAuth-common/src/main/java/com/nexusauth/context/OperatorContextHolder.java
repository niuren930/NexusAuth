package com.nexusauth.context;

/**
 * 当前操作上下文
 *
 * 用于保存一次请求生命周期中的当前操作用户ID
 * 主要供数据库机审计字段自动填充使用
 *
 * 0L 表示 系统操作
 *
 * @author niuren
 * @date 2026-10-03 17:01
 */
public final class OperatorContextHolder {

    /**
     * 系统操作人ID
     */
    public static final Long SYSTEM_USER_ID = 0L;

    private static final ThreadLocal<Long> OPERATOR_CONTEXT = new ThreadLocal<>();

    private OperatorContextHolder(){}

    /**
     * 设置当前操作人用户id
     * @param userId 用户id
     */
    public static void setUserId(Long userId) {
        OPERATOR_CONTEXT.set(userId);
    }

    /**
     * 获取当前操作人用户ID。
     *
     * @return 当前用户ID；不存在时返回null
     */
    public static Long getUserId() {
        return OPERATOR_CONTEXT.get();
    }

    /**
     * 获取当前操作人ID。
     *
     * 如果当前不存在登录用户，则认为是系统操作。
     *
     * @return 用户ID或0
     */
    public static Long getUserIdOrSystem() {
        Long userId = getUserId();

        return userId != null
                ? userId
                : SYSTEM_USER_ID;
    }

    /**
     * 清理当前线程中的操作人上下文。
     */
    public static void clear() {
        OPERATOR_CONTEXT.remove();
    }

}
