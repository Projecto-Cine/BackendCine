package com.cine.demo.config;

import com.cine.demo.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Auth (public)
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                // Client lookup — any authenticated staff member
                .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR", "TICKET")
                // Consultas públicas (cine)
                .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/screenings/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/theaters/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/seats/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/merchandise/**").permitAll()
                // Gestión de usuarios → solo ADMIN y SUPERVISOR
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "SUPERVISOR")
                // Gestión de contenido → ADMIN, SUPERVISOR y OPERATOR
                .requestMatchers(HttpMethod.POST, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/theaters/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/theaters/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.DELETE, "/api/theaters/**").hasAnyRole("ADMIN")
                // Reportes, dashboard y auditoría → solo ADMIN y SUPERVISOR
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/audit-logs/**").hasAnyRole("ADMIN", "SUPERVISOR")
                // Incidencias → ADMIN, SUPERVISOR y MAINTENANCE
                .requestMatchers("/api/incidents/**").hasAnyRole("ADMIN", "SUPERVISOR", "MAINTENANCE")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                // No token or invalid token → 401 so the frontend redirects to /login
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                // Valid token but insufficient role → 403
                .accessDeniedHandler((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}