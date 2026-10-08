package com.cine.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS con lista blanca explicita.
 *
 * Antes se usaba setAllowedOriginPatterns("*") junto a setAllowCredentials(true):
 * con patrones, Spring refleja el Origin que envie el navegador, de modo que
 * cualquier web podia hacer peticiones con credenciales contra la API. Aqui se
 * usa setAllowedOrigins con origenes exactos, que es lo unico compatible de
 * forma segura con allowCredentials.
 */
@Configuration
public class CorsConfig {

    private final List<String> allowedOrigins;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String allowedOriginsCsv) {
        this.allowedOrigins = Arrays.stream(allowedOriginsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (this.allowedOrigins.isEmpty()) {
            throw new IllegalStateException(
                    "app.cors.allowed-origins no puede estar vacio. Declara los origenes del frontend.");
        }
        if (this.allowedOrigins.contains("*")) {
            throw new IllegalStateException(
                    "app.cors.allowed-origins no admite \"*\": es incompatible con credenciales. "
                    + "Declara los origenes exactos separados por comas.");
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Stripe-Signature"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Orden explicito: sin el, el CorsFilter queda con la precedencia mas baja y
     * correria despues de la cadena de Spring Security, que ya habria rechazado
     * el preflight.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        // Sin inyectar por tipo: Spring MVC registra mvcHandlerMappingIntrospector,
        // que tambien implementa CorsConfigurationSource, asi que el tipo es
        // ambiguo. La llamada directa la intercepta el proxy de @Configuration y
        // devuelve el mismo singleton.
        return new CorsFilter(corsConfigurationSource());
    }
}
