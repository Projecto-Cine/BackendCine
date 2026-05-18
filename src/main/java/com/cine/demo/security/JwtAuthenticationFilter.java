package com.cine.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/employee-login",
            "/api/auth/register",
            "/api/payments/webhook"
    );

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicPath(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response, "Missing or invalid authentication token");
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            Map<String, String> claims = jwtUtil.validateAndExtract(token);
            String sub = claims.get("sub");
            String email = claims.get("email");
            String roleStr = claims.get("role");
            if (sub == null || email == null || roleStr == null) {
                writeUnauthorized(response, "Token inválido: faltan campos requeridos");
                return;
            }
            AuthenticatedUser user = AuthenticatedUser.builder()
                    .id(Long.parseLong(sub))
                    .email(email)
                    .role(roleStr)
                    .build();
            if (path.startsWith("/api/dashboard") && !"GERENCIA".equals(roleStr)) {
                writeForbidden(response, "Access denied: insufficient permissions");
                return;
            }
            if (path.startsWith("/api/incidents")
                    && !"GERENCIA".equals(roleStr) && !"MANTENIMIENTO".equals(roleStr)) {
                writeForbidden(response, "Access denied: insufficient permissions");
                return;
            }
            AuthContext.set(user);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.email(), null, List.of(new SimpleGrantedAuthority(user.role())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException ex) {
            writeUnauthorized(response, ex.getMessage());
        } catch (Exception ex) {
            writeUnauthorized(response, "Token inválido o malformado");
        } finally {
            AuthContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        String body = String.format(
                "{\"message\":\"%s\",\"timestamp\":\"%s\"}",
                escapeJson(message), java.time.LocalDateTime.now());
        response.getWriter().write(body);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String body = String.format(
                "{\"message\":\"%s\",\"timestamp\":\"%s\"}",
                escapeJson(message), java.time.LocalDateTime.now());
        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
