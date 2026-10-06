package com.nexusauth.handler.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.nexusauth.context.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * NexusAuth MyBatis-Plus 多租户处理器
 *
 * 用于告诉 MP：
 * 1. 当前租户是谁？
 * 2. 租户字段叫什么？
 * 3. 哪些表无需租户隔离
 *
 * @author niuren
 * @date 2026-10-03 17:57
 */
@Component
public class NexusTenantLineHandler implements TenantLineHandler {

    /**
     * 平台级表。
     *
     * 这些表不直接属于某一个租户，
     * 因此不参与 tenant_id 自动隔离。
     */
    private static final Set<String> IGNORE_TABLES = Set.of(
            "na_user",
            "na_tenant",
            // 应用资源是全局定义，表中没有 tenant_id；授权关系表继续参与租户隔离。
            "na_resource"
    );

    /**
     * 获取当前租户ID。
     */
    @Override
    public Expression getTenantId() {

        Long tenantId =
                TenantContextHolder.requireTenantId();

        return new LongValue(tenantId);
    }

    /**
     * 数据库中的租户字段名称。
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 判断当前表是否忽略多租户拦截。
     *
     * true  = 不拼 tenant_id
     * false = 自动拼 tenant_id
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return IGNORE_TABLES.contains(tableName);
    }
}
