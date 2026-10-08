package com.nexusauth.interceptor;

import com.nexusauth.constant.NexusHeaderConstants;
import com.nexusauth.context.OperatorContextHolder;
import com.nexusauth.context.TenantContextHolder;
import com.sun.net.httpserver.HttpServer;
import feign.Feign;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.Target;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feign 服务凭证与上下文传输的回归测试。
 * <p>
 * 使用真实 Feign 和本机临时 HTTP 服务，验证实际发出的 Header；
 * 不启动 Spring Boot，不访问数据库、Redis 或 Nacos。
 *
 * @author niuren
 * @date 2026-10-08 23:24
 */
public class NexusFeignContextInterceptorTest {

    /** 合成测试数据，没有真实服务访问能力。 */
    private static final String TEST_TOKEN = "T".repeat(43);

    private final NexusFeignContextInterceptor interceptor =
            new NexusFeignContextInterceptor(TEST_TOKEN);

    private final AtomicReference<Map<String, List<String>>> receivedHeaders =
            new AtomicReference<>();

    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        clearContext();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/probe", exchange -> {
            // HTTP Header 名不区分大小写；保留列表以断言没有重复凭证或身份。
            Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            exchange.getRequestHeaders().forEach((name, values) ->
                    headers.put(name, List.copyOf(values)));
            receivedHeaders.set(headers);

            byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (var body = exchange.getResponseBody()) {
                body.write(response);
            } finally {
                exchange.close();
            }
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        try {
            if (server != null) {
                server.stop(0);
            }
        } finally {
            clearContext();
        }
    }

    @Test
    void loginBeforeUserContextExistsMustStillCarryServiceCredential() {
        // 登录前即使模板带有旧身份或大小写不同的同名 Header，也只能发送服务凭证。
        assertEquals("ok", clientFor("nexusauth-system", Map.of(
                NexusHeaderConstants.SERVICE_TOKEN.toLowerCase(Locale.ROOT), List.of("untrusted"),
                NexusHeaderConstants.USER_ID.toLowerCase(Locale.ROOT), List.of("9991"),
                NexusHeaderConstants.TENANT_ID.toLowerCase(Locale.ROOT), List.of("9992")
        )).probe());

        assertHeader(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        assertMissingHeader(NexusHeaderConstants.USER_ID);
        assertMissingHeader(NexusHeaderConstants.TENANT_ID);
    }

    @Test
    void selectedTenantMustTransmitCurrentUserAndTenant() {
        OperatorContextHolder.setUserId(1001L);
        TenantContextHolder.setTenantId(2001L);

        clientFor("nexusauth-system", Map.of()).probe();

        assertHeader(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        assertHeader(NexusHeaderConstants.USER_ID, "1001");
        assertHeader(NexusHeaderConstants.TENANT_ID, "2001");
    }

    @Test
    void userWithoutSelectedTenantMustNotInventTenantContext() {
        OperatorContextHolder.setUserId(1001L);

        clientFor("nexusauth-system", Map.of()).probe();

        assertHeader(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        assertHeader(NexusHeaderConstants.USER_ID, "1001");
        assertMissingHeader(NexusHeaderConstants.TENANT_ID);
    }

    @Test
    void existingReservedHeadersMustBeReplacedByTrustedContext() {
        OperatorContextHolder.setUserId(1001L);
        TenantContextHolder.setTenantId(2001L);
        ProbeApi client = clientFor("nexusauth-system", Map.of(
                NexusHeaderConstants.SERVICE_TOKEN, List.of("untrusted-one", "untrusted-two"),
                NexusHeaderConstants.USER_ID, List.of("9991", "9992"),
                NexusHeaderConstants.TENANT_ID, List.of("9993")
        ));

        client.probe();

        assertHeader(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        assertHeader(NexusHeaderConstants.USER_ID, "1001");
        assertHeader(NexusHeaderConstants.TENANT_ID, "2001");
    }

    @Test
    void repeatedCallsMustNotReusePreviousRequestIdentity() {
        ProbeApi client = clientFor("nexusauth-system", Map.of());
        OperatorContextHolder.setUserId(1001L);
        TenantContextHolder.setTenantId(2001L);
        client.probe();
        assertHeader(NexusHeaderConstants.TENANT_ID, "2001");

        // 同一 Feign Client 的下一次请求不能沿用上一请求的 ThreadLocal 身份。
        clearContext();
        client.probe();

        assertHeader(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        assertMissingHeader(NexusHeaderConstants.USER_ID);
        assertMissingHeader(NexusHeaderConstants.TENANT_ID);
    }

    @Test
    void otherServicesMustNotReceiveSystemCredentialOrInternalIdentity() {
        OperatorContextHolder.setUserId(1001L);
        TenantContextHolder.setTenantId(2001L);
        clientFor("another-service", Map.of(
                NexusHeaderConstants.SERVICE_TOKEN, List.of(TEST_TOKEN),
                NexusHeaderConstants.USER_ID, List.of("9991"),
                NexusHeaderConstants.TENANT_ID, List.of("9992"),
                "X-Trace-Id", List.of("test-trace")
        )).probe();

        assertMissingHeader(NexusHeaderConstants.SERVICE_TOKEN);
        assertMissingHeader(NexusHeaderConstants.USER_ID);
        assertMissingHeader(NexusHeaderConstants.TENANT_ID);
        assertHeader("X-Trace-Id", "test-trace");
    }

    @Test
    void unknownTargetMustRemoveReservedHeadersWithoutInjectingCredential() {
        RequestTemplate template = new RequestTemplate();
        template.header(NexusHeaderConstants.SERVICE_TOKEN, TEST_TOKEN);
        template.header(NexusHeaderConstants.USER_ID, "9991");
        template.header(NexusHeaderConstants.TENANT_ID, "9992");

        interceptor.apply(template);

        assertFalse(template.headers().containsKey(NexusHeaderConstants.SERVICE_TOKEN));
        assertFalse(template.headers().containsKey(NexusHeaderConstants.USER_ID));
        assertFalse(template.headers().containsKey(NexusHeaderConstants.TENANT_ID));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"short", "00000000-0000-0000-0000-000000000000"})
    void invalidConfiguredCredentialMustFailBeforeCallsArePossible(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new NexusFeignContextInterceptor(value));
    }

    private ProbeApi clientFor(String serviceName, Map<String, List<String>> existingHeaders) {
        return Feign.builder()
                // 模拟其他模板设置预先提供的 Header，生产拦截器随后必须重建保留字段。
                .requestInterceptor(template -> existingHeaders.forEach((name, values) ->
                        template.header(name, values)))
                .requestInterceptor(interceptor)
                .target(new Target.HardCodedTarget<>(ProbeApi.class, serviceName, baseUrl));
    }

    private void assertHeader(String name, String expectedValue) {
        assertEquals(List.of(expectedValue), receivedHeaders.get().get(name));
    }

    private void assertMissingHeader(String name) {
        assertFalse(receivedHeaders.get().containsKey(name));
    }

    private void clearContext() {
        TenantContextHolder.clear();
        OperatorContextHolder.clear();
    }

    /** 最小 HTTP 契约，只用于验证 Header 穿过实际 Feign 调用链。 */
    public interface ProbeApi {

        @RequestLine("GET /probe")
        String probe();
    }
}
