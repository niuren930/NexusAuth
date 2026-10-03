package com.nexusauth.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexusauth.domain.entity.TenantMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 租户成员关系表 Mapper
 *
 * @author niuren
 * @date 2026-10-03 17:30
 */
public interface TenantMemberMapper extends BaseMapper<TenantMember> {

    /**
     * 跨租户查询某用户加入的全部成员关系。
     * <p>
     * 平台级问题（例如「我的租户列表」）不能被 tenant_id 条件限制，
     * 否则只能查到当前租户下的关系。
     * 因此这里通过 {@code @InterceptorIgnore(tenantLine = "true")}
     * 跳过多租户拦截器，按 user_id 全表查询。
     * <p>
     * 注意：该方法会突破租户隔离，只允许在明确的平台级业务中使用，
     * 调用前必须自行校验当前操作人就是 userId 对应的用户。
     *
     * @param userId 用户ID
     * @return 该用户在所有租户下的成员关系
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM na_tenant_member WHERE user_id = #{userId}")
    List<TenantMember> selectByUserIdCrossTenant(@Param("userId") Long userId);
}
