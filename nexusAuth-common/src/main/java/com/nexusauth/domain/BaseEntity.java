package com.nexusauth.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * 这里采用mp的自动填充，请看 NexusMetaObjectHandler.java
 *
 * @author niuren
 * @date 2026-10-03 14:29
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {
    /**
     * 创建人用户ID
     * 0表示系统创建
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 最后更新人用户ID
     * 0表示系统更新
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
