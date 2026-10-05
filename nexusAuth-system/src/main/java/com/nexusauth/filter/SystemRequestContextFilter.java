package com.nexusauth.filter;

import com.nexusauth.constant.NexusHeaderConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * System 服务内部请求上下文恢复过滤器
 * <p>
 * Auth 服务通过 Feign 调用 System 服务时，会通过内部 Header 传递当前登录用户和租户。
 * <p>
 * 本过滤器负责：
 * <p>
 * 1. 读取 X-Nexus-User-Id；
 * 2. 恢复 OperatorContextHolder；
 * 3. 读取 X-Nexus-Tenant-Id；
 * 4. 恢复 TenantContextHolder；
 * 5. 请求结束后清理 ThreadLocal。
 * <p>
 * 恢复 TenantContextHolder 后，
 * MyBatis-Plus TenantLineInnerInterceptor 即可自动读取当前 tenantId 并为租户表 SQL 添加隔离条件。
 * <p>
 * 安全说明：
 * 当前 默认 system 属于内部服务，不应直接暴露到公网。
 * <p>
 * 待接入 Gateway 后，需要由 Gateway 清理客户端伪造的 X-Nexus-* Header，并只允许可信内部上下文继续向下传递
 *
 * @author niuren
 * @date 2026-10-05 21:29
 */
@Component
public class SystemRequestContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String userIdHeader = request.getHeader(NexusHeaderConstants.USER_ID);
            String tenantIdHeader = request.getHeader(NexusHeaderConstants.TENANT_ID);

            /*
             * 恢复当前操作用户
             *
             * 查询用户信息，不一定存在 USER_ID Header
             */
            if (StringUtils.hasText(userIdHeader)) {
                OperatorContextHolder.setUserId(Long.valueOf(userIdHeader));
            }

            /*
             * 恢复当前租户
             *
             * 登录、查询“我的租户”等平台级操作可能没有 tenantId，因此这里允许 Header 不存在
             */
            if (StringUtils.hasText(tenantIdHeader)) {
                // 有租户上下文时肯定存在登录用户
                if (!StringUtils.hasText(userIdHeader)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "租户上下文无效");

                    return;
                }
                TenantContextHolder.setTenantId(Long.valueOf(tenantIdHeader));
            }

            filterChain.doFilter(
                    request,
                    response
            );
        } catch (NumberFormatException exception) {

            /*
             * 内部 Header 应该只能包含 Long 类型 ID。
             * 出现非法值时直接拒绝请求，不允许带着错误上下文继续执行数据库操作。
             */
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "上下文无效"
            );

        } finally {
            TenantContextHolder.clear();
            OperatorContextHolder.clear();
        }
    }
}
