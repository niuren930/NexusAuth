package com.nexusauth.filter;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.nexusauth.constant.AuthSessionConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 服务请求上下文恢复过滤器
 * <p>
 * 每个请求自动恢复登录上下文
 * <p>
 * 主要职责：
 * <p>
 * 1. 如果当前请求已经处于 Sa-Token 登录状态，将 loginId 恢复到 OperatorContextHolder；
 * <p>
 * 2. 如果当前 Token-Session 已经选择租户，将 currentTenantId 恢复到 TenantContextHolder；
 * <p>
 * 3. 请求结束后必须清理 ThreadLocal，防止 Tomcat 线程池复用造成用户或租户上下文串线。
 * <p>
 * 注意：
 * 本过滤器不是全局登录鉴权过滤器。
 * 它不会强制所有接口必须登录：
 * - /auth/login 未登录时仍然可以正常访问；
 * - 需要登录的接口当前仍由 StpUtil.checkLogin() 校验；Gateway 统一 Token 鉴权留到后期
 *
 * @author niuren
 * @date 2026-10-05 18:29
 */
@Component
public class AuthRequestContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            /*
                当前请求存在有效登录态时，才恢复上下文
             */
            if (StpUtil.isLogin()) {
                long userId = StpUtil.getLoginIdAsLong();

                // 恢复当前操作人
                OperatorContextHolder.setUserId(userId);

                // 当前租户属于 Token 级别上下文，所以从 token-session 中获取
                // 同一用户使用不同token，可以分别停留在不同的租户
                SaSession tokenSession = StpUtil.getTokenSession();
                Object tenantIdValue = tokenSession.get(AuthSessionConstants.TENANT_ID);

                // 登录后未选择租户时，tenantIdValue 为 null
                // 此时不设置租户上下午TenantContextHolder，平台级接口也可以正常运行，只有真正访问租户级数据时再要求必须存在租户
                if (tenantIdValue instanceof Number number) {
                    TenantContextHolder.setTenantId(number.longValue());
                }
            }

            /*
             * 当前请求正式进入 Controller / Service。
             */
            filterChain.doFilter(request, response);
        } finally {

            /*
             * ThreadLocal 的清理必须放在 finally 中。
             *
             * 即使 Controller、Service 或 Feign 调用发生异常，
             * 也必须保证上下文被清除。
             */
            TenantContextHolder.clear();
            OperatorContextHolder.clear();
        }

    }
}
