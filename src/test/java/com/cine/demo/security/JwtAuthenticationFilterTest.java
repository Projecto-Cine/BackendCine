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

import org.springframework.security.core.context.SecurityContextHolder;

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
        SecurityContextHolder.clearContext();
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

    /**
     * El filtro ya no responde 401 por si mismo cuando falta la cabecera: deja
     * pasar sin autenticar y es la cadena de autorizacion la que decide si esa
     * ruta exigia sesion. Asi no hay dos listas de rutas publicas que divergan.
     */
    @Test
    void doFilter_delegatesWithoutAuthenticating_whenAuthorizationHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(AuthContext.get()).isNull();
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_delegatesWithoutAuthenticating_whenHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Basic xyz");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtUtil);
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
        claims.put("role", "CLIENT");
        when(jwtUtil.validateAndExtract("valid.token.payload")).thenReturn(claims);

        AuthenticatedUser[] capturedAuth = new AuthenticatedUser[1];
        doAnswer(inv -> {
            capturedAuth[0] = AuthContext.get();
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(capturedAuth[0]).isNotNull();
        assertThat(capturedAuth[0].id()).isEqualTo(5L);
        assertThat(capturedAuth[0].email()).isEqualTo("ana@cine.com");
        assertThat(capturedAuth[0].role()).isEqualTo(Role.CLIENT.name());
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
    void doFilter_writesJsonError_whenTokenIsPresentButInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer roto");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtUtil.validateAndExtract("roto")).thenThrow(new InvalidTokenException("Invalid token format"));

        filter.doFilter(request, response, chain);

        String body = response.getContentAsString();
        assertThat(body).contains("\"message\":");
        assertThat(body).contains("\"timestamp\":");
        assertThat(response.getContentType()).contains("application/json");
    }

    /**
     * El principal debe ser el AuthenticatedUser completo y no solo el email:
     * OwnershipGuard necesita el id para resolver si un recurso es del usuario.
     */
    @Test
    void doFilter_setsAuthenticatedUserAsPrincipal_notJustTheEmail() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/5");
        request.addHeader("Authorization", "Bearer valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "5");
        claims.put("email", "ana@cine.com");
        claims.put("role", "CLIENT");
        when(jwtUtil.validateAndExtract("valido")).thenReturn(claims);

        Object[] principal = new Object[1];
        doAnswer(inv -> {
            principal[0] = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        assertThat(principal[0]).isInstanceOf(AuthenticatedUser.class);
        assertThat(((AuthenticatedUser) principal[0]).id()).isEqualTo(5L);
    }

    @Test
    void doFilter_grantsTheRoleClaimAsAuthority() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/shifts");
        request.addHeader("Authorization", "Bearer valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "9");
        claims.put("email", "cajero@cine.com");
        claims.put("role", "CAJERO");
        when(jwtUtil.validateAndExtract("valido")).thenReturn(claims);

        String[] authority = new String[1];
        doAnswer(inv -> {
            authority[0] = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().iterator().next().getAuthority();
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        assertThat(authority[0]).isEqualTo("CAJERO");
    }

    @Test
    void doFilter_returns401_whenSubjectIsNotANumericUserId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer raro");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "no-es-un-id");
        claims.put("email", "x@t.com");
        claims.put("role", "CLIENT");
        when(jwtUtil.validateAndExtract("raro")).thenReturn(claims);

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void doFilter_returns401_whenClaimsAreIncomplete() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("Authorization", "Bearer incompleto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> claims = new HashMap<>();
        claims.put("sub", "1");
        // sin email ni role
        when(jwtUtil.validateAndExtract("incompleto")).thenReturn(claims);

        filter.doFilter(request, response, chain);

        verifyNoInteractions(chain);
        assertThat(response.getStatus()).isEqualTo(401);
    }
}
