package com.cine.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Autentica la peticion a partir del JWT. No autoriza.
 *
 * La version anterior decidia el acceso aqui, comparando prefijos de ruta a
 * mano (path.startsWith de /api/dashboard y similares). Eso tenia dos
 * problemas: solo cubria dos rutas de veinte, y ponia la politica de acceso en
 * un sitio donde no se ve al leer el endpoint. Ahora la autorizacion vive en
 * SecurityConfig y en las anotaciones de cada controlador, y este filtro se
 * limita a poblar el SecurityContext.
 *
 * Las rutas publicas no se listan aqui: las decide SecurityConfig. Este filtro
 * deja pasar sin autenticar cualquier peticion sin cabecera Authorization, y es
 * la cadena de autorizacion la que responde 401 si esa ruta la exigia. Asi no
 * hay dos listas de rutas publicas que puedan divergir.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            Map<String, String> claims = jwtUtil.validateAndExtract(token);
            String sub = claims.get("sub");
            String email = claims.get("email");
            String role = claims.get("role");

            if (sub == null || email == null || role == null) {
                writeUnauthorized(response, "Invalid token: missing required fields");
                return;
            }

            AuthenticatedUser user = AuthenticatedUser.builder()
                    .id(Long.parseLong(sub))
                    .email(email)
                    .role(role)
                    .build();

            // El principal es el AuthenticatedUser completo, no solo el email:
            // las comprobaciones de propiedad necesitan el id del usuario.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user, null, List.of(new SimpleGrantedAuthority(role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            AuthContext.set(user);
            try {
                filterChain.doFilter(request, response);
            } finally {
                AuthContext.clear();
            }

        } catch (InvalidTokenException ex) {
            writeUnauthorized(response, ex.getMessage());
        } catch (NumberFormatException ex) {
            writeUnauthorized(response, "Invalid token: subject is not a user id");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"message\":\"%s\",\"timestamp\":\"%s\"}",
                escapeJson(message), java.time.LocalDateTime.now()));
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
