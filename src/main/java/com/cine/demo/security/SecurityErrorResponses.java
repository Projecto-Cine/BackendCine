package com.cine.demo.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Respuestas JSON para los dos fallos de seguridad, con el codigo correcto.
 *
 * Por defecto Spring Security responde 403 tambien cuando no hay sesion
 * ninguna, porque el usuario anonimo simplemente carece del permiso. Esa
 * distincion importa aqui: el cliente HTTP del frontend trata el 401 como
 * sesion caducada, limpia el token y emite el evento auth:expired. Si el
 * backend contesta 403 a una peticion sin credenciales, el usuario se queda con
 * una sesion muerta en pantalla y sin forma de saber que tiene que volver a
 * entrar.
 *
 *   401 — no se sabe quien eres: falta el token o no es valido.
 *   403 — se sabe quien eres y no te corresponde este recurso.
 */
public final class SecurityErrorResponses {

    private SecurityErrorResponses() {}

    public static AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) ->
                write(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication required: missing or invalid token");
    }

    public static AccessDeniedHandler forbiddenHandler() {
        return (request, response, accessDeniedException) ->
                write(response, HttpServletResponse.SC_FORBIDDEN,
                        "Access denied: insufficient permissions");
    }

    private static void write(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"message\":\"%s\",\"timestamp\":\"%s\"}", message, LocalDateTime.now()));
    }
}
