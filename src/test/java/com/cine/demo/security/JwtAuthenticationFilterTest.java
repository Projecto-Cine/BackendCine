package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtil jwtUtil;
    private JwtAuthenticationFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        filter = new JwtAuthenticationFilter(jwtUtil);
        chain = mock(FilterChain.class);
    }

    @AfterEach
    void cleanup() {
        AuthContext.clear();
    }

    @Test
    void doFilter_allowsLoginEndpoint_withoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_allowsRegisterEndpoint_withoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/register");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_returns401_whenAuthorizationHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Missing or invalid authentication token");
    }

    @Test
    void doFilter_returns401_whenHeaderDoesNotStartWithBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Basic xyz");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void doFilter_returns401_whenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer fake.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtUtil.validateAndExtract("fake.token.here"))
                .thenThrow(new InvalidTokenException("Token caducado"));

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Token caducado");
    }

    @Test
    void doFilter_passesThrough_andSetsAuthContext_whenTokenValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer valid.token.payload");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "5");
        claims.put("email", "ana@cine.com");
        claims.put("role", "CLIENTE");
        when(jwtUtil.validateAndExtract("valid.token.payload")).thenReturn(claims);

        AuthenticatedUser[] capturedAuth = new AuthenticatedUser[1];
        doAnswer(inv -> {
            capturedAuth[0] = AuthContext.get();
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(capturedAuth[0]).isNotNull();
        assertThat(capturedAuth[0].getId()).isEqualTo(5L);
        assertThat(capturedAuth[0].getEmail()).isEqualTo("ana@cine.com");
        assertThat(capturedAuth[0].getRole()).isEqualTo(Role.CLIENTE);
    }

    @Test
    void doFilter_clearsAuthContext_afterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer x.y.z");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "1");
        claims.put("email", "x@t.com");
        claims.put("role", "ADMIN");
        when(jwtUtil.validateAndExtract("x.y.z")).thenReturn(claims);

        filter.doFilter(request, response, chain);

        assertThat(AuthContext.get()).isNull();
    }

    @Test
    void doFilter_writesJsonError_withApiResponseShape() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        String body = response.getContentAsString();
        assertThat(body).contains("\"message\":");
        assertThat(body).contains("\"timestamp\":");
        assertThat(response.getContentType()).contains("application/json");
    }
}
