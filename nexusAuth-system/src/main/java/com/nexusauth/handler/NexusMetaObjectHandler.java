package com.nexusauth.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 公共字段自动填充处理器。
 * <p>
 * 自动维护：
 * createdBy
 * createdAt
 * updatedBy
 * updatedAt
 *
 * @author niuren
 * @date 2026-10-03 17:09
 */
@Component
public class NexusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增数据时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        Long operatorId = OperatorContextHolder.getUserIdOrSystem();

        LocalDateTime now = LocalDateTime.now();

        this.strictInsertFill(metaObject, "createdBy", Long.class, operatorId);
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedBy", Long.class, operatorId);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);

        // 当前实体如果存在 tenantId 字段，则自动填充租户ID
        if (metaObject.hasSetter("tenantId")) {

            Long tenantId = TenantContextHolder.getTenantId();

            if (tenantId != null) {
                this.strictInsertFill(
                        metaObject,
                        "tenantId",
                        Long.class,
                        tenantId
                );
            }
        }

    }

    /**
     * 更新数据时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long operatorId = OperatorContextHolder.getUserIdOrSystem();

        LocalDateTime now = LocalDateTime.now();

        this.strictUpdateFill(metaObject, "updatedBy", Long.class, operatorId);
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, now);

        metaObject.setValue("updatedBy", operatorId);
        metaObject.setValue("updatedAt", now);
    }
}
