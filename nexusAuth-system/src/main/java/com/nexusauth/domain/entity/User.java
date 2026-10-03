package com.nexusauth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexusauth.domain.BaseEntity;
import com.nexusauth.domain.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * NexusAuth 全局用户实体。
 * <p>
 * 用户是平台级全局身份，不直接绑定租户
 * 用户与平台的关系通过 TenantMember 维护
 *
 * @author niuren
 * @date 2026-10-03 16:41
 */
@TableName("na_user")
@Data
public class User extends BaseEntity {

    /**
     * 用户ID。
     * <p>
     * 使用 MyBatis-Plus ASSIGN_ID 自动生成分布式ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 登录用户名，全局唯一。
     */
    private String username;

    /**
     * 密码哈希。
     * <p>
     * 这里只保存 BCrypt / Argon2 等算法生成的密码哈希，
     * 绝不能保存明文密码。
     */
    private String passwordHash;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 邮箱地址。
     */
    private String email;

    /**
     * 手机号。
     */
    private String mobile;

    /**
     * 头像地址。
     */
    private String avatar;

    /**
     * 用户状态：
     * 1 - 正常
     * 2 - 禁用
     * 3 - 锁定
     * 4 - 注销
     */
    private UserStatus status;

    /**
     * 最后一次修改密码时间。
     */
    private LocalDateTime passwordUpdatedAt;

    /**
     * 最后登录时间。
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP。
     */
    private String lastLoginIp;

}
