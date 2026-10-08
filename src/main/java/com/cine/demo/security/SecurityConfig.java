package com.cine.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cadena de seguridad.
 *
 * La version anterior terminaba en anyRequest().permitAll(), de modo que Spring
 * Security autorizaba absolutamente todo y la unica barrera real era un par de
 * comparaciones de prefijo dentro del filtro JWT. Con dieciocho de veinte
 * controladores sin anotacion, cualquier cliente registrado podia invocar los
 * endpoints de administracion.
 *
 * Ahora el criterio es el inverso: todo exige autenticacion salvo lo que se
 * declara publico aqui de forma explicita, y cada controlador declara ademas
 * que rol necesita mediante las anotaciones de security.access.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Sin CSRF porque no hay cookies de sesion: la credencial es un
            // Bearer token que el navegador no adjunta automaticamente.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            // Sin esto Spring Security responde 403 tambien cuando no hay
            // sesion, y el frontend no distingue "vuelve a entrar" de "esto no
            // es para ti".
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(SecurityErrorResponses.unauthorizedEntryPoint())
                    .accessDeniedHandler(SecurityErrorResponses.forbiddenHandler()))
            .authorizeHttpRequests(auth -> auth
                // El preflight no lleva credenciales. Lo resuelve CorsFilter.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Autenticacion y alta de clientes.
                .requestMatchers(HttpMethod.POST,
                        "/api/auth/login",
                        "/api/auth/employee-login",
                        "/api/auth/register").permitAll()

                // Stripe firma el webhook con su propio secreto; no lleva JWT.
                .requestMatchers(HttpMethod.POST, "/api/payments/webhook").permitAll()

                // Catalogo publico: la tienda debe poder mostrar cartelera y
                // horarios a quien todavia no tiene cuenta.
                .requestMatchers(HttpMethod.GET,
                        "/api/movies",
                        "/api/movies/active",
                        "/api/movies/{id}",
                        "/api/screenings",
                        "/api/screenings/upcoming",
                        "/api/screenings/{id}",
                        "/api/screenings/movie/{movieId}",
                        "/api/screenings/{id}/seats").permitAll()

                // Documentacion de la API.
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**").permitAll()

                // Todo lo demas exige sesion valida. El rol concreto lo exige
                // cada controlador con ManagementOnly, BoxOffice y companhia.
                .anyRequest().authenticated()
            )
            // El limite de intentos va antes de autenticar: frenar la fuerza
            // bruta no debe costar una consulta a base de datos por intento.
            .addFilterBefore(new AuthRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
