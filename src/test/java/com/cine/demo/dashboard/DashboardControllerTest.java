package com.cine.demo.dashboard;

import com.cine.demo.controller.DashboardController;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.service.DashboardService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import({DashboardControllerTest.TestSecurityConfig.class, GlobalExceptionHandler.class})
class DashboardControllerTest {

    @Configuration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                            .accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN)))
                    .build();
        }
    }

    @Autowired private MockMvc mockMvc;
    @MockitoBean private DashboardService dashboardService;

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getDashboard_gerencia_returns200() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void getDashboard_cajero_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getDashboard_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyDashboard_gerencia_returns200() throws Exception {
        mockMvc.perform(get("/api/dashboard/yearly"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "LIMPIEZA")
    void getYearlyDashboard_limpieza_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/yearly"))
                .andExpect(status().isForbidden());
    }
}
