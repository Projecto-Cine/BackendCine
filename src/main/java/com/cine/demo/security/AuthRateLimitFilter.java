package com.cine.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limita los intentos contra los endpoints de autenticacion.
 *
 * Sin esto, login y alta de clientes aceptan peticiones sin limite, lo que deja
 * la puerta abierta a probar credenciales filtradas en masa y a crear cuentas
 * de forma automatizada. Es la contramedida que faltaba junto a la politica de
 * contrasenas: una contrasena de ocho caracteres solo sirve si no se pueden
 * probar miles por minuto.
 *
 * Es un contador en memoria con ventana deslizante, por IP y por ruta. Eso
 * basta para el despliegue actual, de una sola instancia, y tiene la ventaja de
 * no anadir dependencias ni infraestructura. Con varias instancias el limite
 * seria por instancia y habria que mover el contador a un almacen compartido,
 * tipo Redis; queda anotado aqui para que la limitacion sea explicita y no una
 * sorpresa.
 */
@Slf4j
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    /** Por encima de este numero de IPs distintas se purgan las ventanas vencidas. */
    private static final int CLEANUP_THRESHOLD = 1_000;

    private final Map<String, Deque<Instant>> attempts = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean isAuthEndpoint = path.equals("/api/auth/login")
                || path.equals("/api/auth/employee-login")
                || path.equals("/api/auth/register");
        return !isAuthEndpoint || !"POST".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String bucket = clientIp(request) + " " + request.getRequestURI();

        if (!tryConsume(bucket)) {
            log.warn("Limite de intentos alcanzado en {}", request.getRequestURI());
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(WINDOW.toSeconds()));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.format(
                    "{\"message\":\"Demasiados intentos. Vuelve a probar en %d minutos.\",\"timestamp\":\"%s\"}",
                    WINDOW.toMinutes(), java.time.LocalDateTime.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean tryConsume(String bucket) {
        Instant now = Instant.now();
        Instant windowStart = now.minus(WINDOW);

        if (attempts.size() > CLEANUP_THRESHOLD) {
            purgeExpired(windowStart);
        }

        Deque<Instant> timestamps = attempts.computeIfAbsent(bucket, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= MAX_ATTEMPTS) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }

    /** Evita que el mapa crezca sin limite con IPs que ya no vuelven. */
    private void purgeExpired(Instant windowStart) {
        attempts.entrySet().removeIf(entry -> {
            Deque<Instant> timestamps = entry.getValue();
            synchronized (timestamps) {
                return timestamps.isEmpty() || timestamps.peekLast().isBefore(windowStart);
            }
        });
    }

    /**
     * Detras de un proxy (Render, Vercel) la IP real viaja en X-Forwarded-For.
     * Se toma la primera entrada, que es el cliente original.
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }
}
