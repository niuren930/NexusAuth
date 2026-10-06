package com.nexusauth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * 当前 Auth → System 调用链的服务凭证值对象
 *
 * @author niuren
 * @date 2026-10-06 22:30
 */
public final class InternalServiceCredential {
    /**
     * 接受 URL 安全字符，长度 43~128。
     * 32 字节安全随机数经过无填充 Base64URL 编码后长度为 43；
     * 此处只能验证形状，随机性由实际生成过程保证。
     */
    private static final Pattern FORMAT = Pattern.compile("[A-Za-z0-9_-]{43,128}");

    private final String value;
    private final byte[] expectedBytes;

    public InternalServiceCredential(String value) {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("内部服务凭证配置缺失或格式无效");
        }
        this.value = value;
        this.expectedBytes = value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 仅供受信任的出站 HTTP 拦截器写入请求头，不用于日志或响应。
     */
    public String asHeaderValue() {
        return value;
    }

    /**
     * 使用 JDK 的字节比较方法；缺失或格式错误的请求凭证直接拒绝。
     */
    public boolean matches(String candidate) {
        if (candidate == null || !FORMAT.matcher(candidate).matches()) {
            return false;
        }
        return MessageDigest.isEqual(
                expectedBytes,
                candidate.getBytes(StandardCharsets.UTF_8)
        );
    }
}
