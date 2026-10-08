package com.cine.demo.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Limite de intentos en los endpoints de autenticacion.
 *
 * Antes no habia ninguno: se podian probar credenciales filtradas sin freno y
 * crear cuentas de forma automatizada. Una politica de contrasenas solo sirve
 * si no se pueden probar miles por minuto.
 */
class AuthRateLimitFilterTest {

    private static final int MAX_ATTEMPTS = 10;

    private AuthRateLimitFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new AuthRateLimitFilter();
        chain = mock(FilterChain.class);
    }

    private static MockHttpServletRequest loginFrom(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr(ip);
        return request;
    }

    @Test
    void allowsAttemptsUpToTheLimit() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(loginFrom("10.0.0.1"), response, chain);
            assertThat(response.getStatus()).isEqualTo(200);
        }

        verify(chain, times(MAX_ATTEMPTS)).doFilter(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void blocksWithTooManyRequestsOnceTheLimitIsExceeded() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            filter.doFilter(loginFrom("10.0.0.2"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        FilterChain freshChain = mock(FilterChain.class);
        filter.doFilter(loginFrom("10.0.0.2"), blocked, freshChain);

        assertThat(blocked.getStatus()).isEqualTo(429);
        verifyNoInteractions(freshChain);
    }

    @Test
    void tellsTheClientWhenToRetry() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            filter.doFilter(loginFrom("10.0.0.3"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        filter.doFilter(loginFrom("10.0.0.3"), blocked, chain);

        assertThat(blocked.getHeader("Retry-After")).isEqualTo("300");
        assertThat(blocked.getContentAsString()).contains("Demasiados intentos");
        assertThat(blocked.getContentType()).contains("application/json");
    }

    /** El limite es por IP: un atacante no debe poder bloquear a los demas. */
    @Test
    void countsEachClientSeparately() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            filter.doFilter(loginFrom("10.0.0.4"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse otherClient = new MockHttpServletResponse();
        filter.doFilter(loginFrom("10.0.0.5"), otherClient, chain);

        assertThat(otherClient.getStatus()).isEqualTo(200);
    }

    /** Y por ruta: agotar el login no debe cerrar el alta de clientes. */
    @Test
    void countsEachEndpointSeparately() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            filter.doFilter(loginFrom("10.0.0.6"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletRequest register = new MockHttpServletRequest("POST", "/api/auth/register");
        register.setRemoteAddr("10.0.0.6");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(register, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    /**
     * Detras de un proxy todas las peticiones comparten remoteAddr, asi que sin
     * leer X-Forwarded-For el limite seria global y un solo atacante dejaria
     * fuera a todo el mundo.
     */
    @Test
    void usesTheForwardedClientIpWhenBehindAProxy() throws Exception {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            MockHttpServletRequest request = loginFrom("10.0.0.7");
            request.addHeader("X-Forwarded-For", "203.0.113.9, 10.0.0.7");
            filter.doFilter(request, new MockHttpServletResponse(), chain);
        }

        MockHttpServletRequest otherRealClient = loginFrom("10.0.0.7");
        otherRealClient.addHeader("X-Forwarded-For", "203.0.113.20, 10.0.0.7");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(otherRealClient, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doesNotLimitEndpointsOutsideAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/movies");
        request.setRemoteAddr("10.0.0.8");

        for (int i = 0; i < MAX_ATTEMPTS * 3; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void doesNotLimitReadsOnTheAuthenticationPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/login");
        request.setRemoteAddr("10.0.0.9");

        for (int i = 0; i < MAX_ATTEMPTS * 2; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, chain);
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void limitsEmployeeLoginToo() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/employee-login");
        request.setRemoteAddr("10.0.0.10");

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            filter.doFilter(request, new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blocked = new MockHttpServletResponse();
        filter.doFilter(request, blocked, chain);

        assertThat(blocked.getStatus()).isEqualTo(429);
    }
}
