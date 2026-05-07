package com.cine.demo.config;

import com.cine.demo.security.JwtAuthenticationFilter;
import com.cine.demo.security.JwtUtil;
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

    private final JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers("/api/clients/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR", "TICKET")
                .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/screenings/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/theaters/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/seats/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/merchandise/**").permitAll()
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/workers/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/socios/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.POST, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR", "OPERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/screenings/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/theaters/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/theaters/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers(HttpMethod.DELETE, "/api/theaters/**").hasAnyRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/audit-logs/**").hasAnyRole("ADMIN", "SUPERVISOR")
                .requestMatchers("/api/incidents/**").hasAnyRole("ADMIN", "SUPERVISOR", "MAINTENANCE")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .accessDeniedHandler((req, res, e) ->
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}