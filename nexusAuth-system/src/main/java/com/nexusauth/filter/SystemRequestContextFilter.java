package com.nexusauth.filter;

import com.nexusauth.constant.NexusHeaderConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.nexusauth.security.InternalServiceCredential;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

/**
 * System 服务内部请求上下文恢复过滤器
 * <p>
 * Auth 服务通过 Feign 调用 System 服务时，会通过内部 Header 传递当前登录用户和租户。
 * <p>
 * 先验证调用服务，再接受其用户和租户上下文；请求结束必须清理
 *
 * @author niuren
 * @date 2026-10-05 21:29
 */
@Component
public class SystemRequestContextFilter extends OncePerRequestFilter {

    private final InternalServiceCredential credential;

    public SystemRequestContextFilter(@Value("${nexusauth.internal.system-token}") String serviceToken) {
        this.credential = new InternalServiceCredential(serviceToken);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        /* 防御性清理，保证拒绝请求也不会继承线程上遗留的身份。 */
        OperatorContextHolder.clear();
        TenantContextHolder.clear();

        try {
            String serviceToken;
            try {
                serviceToken = readSingleHeader(request, NexusHeaderConstants.SERVICE_TOKEN);
            } catch (IllegalArgumentException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "内部服务身份无效");
                return;
            }

            /* 不合法的调用方连上下文解析都不能进入，更不能访问 Controller。 */
            if (!credential.matches(serviceToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "内部服务身份无效");
                return;
            }

            Long userId;
            Long tenantId;
            try {
                userId = parseOptionalPositiveId(
                        readSingleHeader(request, NexusHeaderConstants.USER_ID)
                );
                tenantId = parseOptionalPositiveId(
                        readSingleHeader(request, NexusHeaderConstants.TENANT_ID)
                );
                if (tenantId != null && userId == null) {
                    throw new IllegalArgumentException("租户上下文缺少操作人");
                }
            } catch (IllegalArgumentException exception) {
                /* 不回显原始 Header，协议格式错误统一返回 400。 */
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "内部上下文无效");
                return;
            }

            if (userId != null) {
                OperatorContextHolder.setUserId(userId);
            }
            if (tenantId != null) {
                TenantContextHolder.setTenantId(tenantId);
            }

            /*
             * 仅 Header 解析位于上面的 catch 内。
             * 下游代码的 NumberFormatException 等异常必须正常向上抛出，
             * 不能把数据库/业务错误误判为 Header 格式错误。
             */
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
            OperatorContextHolder.clear();
        }
    }

    /**
     * 同名 Header 必须最多一个值；合并成逗号的值也会在格式校验时被拒绝。
     */
    private String readSingleHeader(HttpServletRequest request, String name) {
        Enumeration<String> values = request.getHeaders(name);
        if (values == null || !values.hasMoreElements()) {
            return null;
        }
        String value = values.nextElement();
        if (values.hasMoreElements()) {
            throw new IllegalArgumentException("内部 Header 重复");
        }
        return value;
    }

    /**
     * 缺失允许，已提供则必须为可表示的正 Long；空字符串不视为缺失。
     */
    private Long parseOptionalPositiveId(String value) {
        if (value == null) {
            return null;
        }
        if (!value.matches("[0-9]{1,19}")) {
            throw new IllegalArgumentException("内部 ID 格式无效");
        }
        long id = Long.parseLong(value);
        if (id <= 0) {
            throw new IllegalArgumentException("内部 ID 必须为正数");
        }
        return id;
    }
}