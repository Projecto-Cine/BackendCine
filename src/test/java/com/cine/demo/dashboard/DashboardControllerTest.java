package com.cine.demo.dashboard;

import com.cine.demo.controller.DashboardController;
import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.dto.response.YearlyDashboardResponseDTO;
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

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    // ── Role-based access ─────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getDashboard_gerencia_returns200() throws Exception {
        given(dashboardService.getDashboardData()).willReturn(dashboardResponse());

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
        given(dashboardService.getYearlyData(Year.now().getValue())).willReturn(yearlyResponse(Year.now().getValue()));

        mockMvc.perform(get("/api/dashboard/yearly"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "LIMPIEZA")
    void getYearlyDashboard_limpieza_returns403() throws Exception {
        mockMvc.perform(get("/api/dashboard/yearly"))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/dashboard ────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getDashboard_returnsSuccessEnvelope() throws Exception {
        given(dashboardService.getDashboardData()).willReturn(dashboardResponse());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dashboard retrieved successfully"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getDashboard_returnsAllDataFields() throws Exception {
        given(dashboardService.getDashboardData()).willReturn(dashboardResponse());

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(jsonPath("$.data.totalRevenue").isNumber())
                .andExpect(jsonPath("$.data.weeklyRevenue").isNumber())
                .andExpect(jsonPath("$.data.totalPurchases").value(50))
                .andExpect(jsonPath("$.data.paidPurchases").value(40))
                .andExpect(jsonPath("$.data.activeScreenings").value(10))
                .andExpect(jsonPath("$.data.confirmedRoomBookings").value(3))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.activeMovies").value(5))
                .andExpect(jsonPath("$.data.unresolvedIncidents").value(2));
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getDashboard_delegatesToService() throws Exception {
        given(dashboardService.getDashboardData()).willReturn(dashboardResponse());

        mockMvc.perform(get("/api/dashboard"));

        verify(dashboardService).getDashboardData();
    }

    // ── GET /api/dashboard/yearly ─────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyData_returns200() throws Exception {
        given(dashboardService.getYearlyData(2026)).willReturn(yearlyResponse(2026));

        mockMvc.perform(get("/api/dashboard/yearly").param("year", "2026"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyData_withExplicitYear_callsServiceWithThatYear() throws Exception {
        given(dashboardService.getYearlyData(2024)).willReturn(yearlyResponse(2024));

        mockMvc.perform(get("/api/dashboard/yearly").param("year", "2024"))
                .andExpect(status().isOk());

        verify(dashboardService).getYearlyData(2024);
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyData_withoutYearParam_usesCurrentYear() throws Exception {
        int currentYear = Year.now().getValue();
        given(dashboardService.getYearlyData(currentYear)).willReturn(yearlyResponse(currentYear));

        mockMvc.perform(get("/api/dashboard/yearly"))
                .andExpect(status().isOk());

        verify(dashboardService).getYearlyData(currentYear);
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyData_returnsSuccessEnvelope() throws Exception {
        given(dashboardService.getYearlyData(2026)).willReturn(yearlyResponse(2026));

        mockMvc.perform(get("/api/dashboard/yearly").param("year", "2026"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Datos anuales obtenidos correctamente"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(authorities = "GERENCIA")
    void getYearlyData_returnsAllDataFields() throws Exception {
        given(dashboardService.getYearlyData(2026)).willReturn(yearlyResponse(2026));

        mockMvc.perform(get("/api/dashboard/yearly").param("year", "2026"))
                .andExpect(jsonPath("$.data.year").value(2026))
                .andExpect(jsonPath("$.data.moviesProjected").value(15))
                .andExpect(jsonPath("$.data.sessionsProjected").value(100))
                .andExpect(jsonPath("$.data.ticketRevenue").isNumber())
                .andExpect(jsonPath("$.data.merchandiseRevenue").isNumber())
                .andExpect(jsonPath("$.data.topMovies").isArray())
                .andExpect(jsonPath("$.data.topMovies[0].movieTitle").value("Avatar"))
                .andExpect(jsonPath("$.data.topProducts").isArray())
                .andExpect(jsonPath("$.data.topProducts[0].productName").value("Popcorn"));
    }

    // ── Fixtures ──────────────────────────────────────────────────────────

    private DashboardResponseDTO dashboardResponse() {
        return DashboardResponseDTO.builder()
                .totalRevenue(new BigDecimal("1000"))
                .weeklyRevenue(new BigDecimal("200"))
                .totalPurchases(50)
                .paidPurchases(40)
                .activeScreenings(10)
                .confirmedRoomBookings(3)
                .totalUsers(100)
                .activeMovies(5)
                .unresolvedIncidents(2)
                .build();
    }

    private YearlyDashboardResponseDTO yearlyResponse(int year) {
        return YearlyDashboardResponseDTO.builder()
                .year(year)
                .moviesProjected(15)
                .sessionsProjected(100)
                .ticketRevenue(new BigDecimal("5000"))
                .merchandiseRevenue(new BigDecimal("1200"))
                .topMovies(List.of(
                        YearlyDashboardResponseDTO.TopMovieDTO.builder()
                                .movieId(1L).movieTitle("Avatar")
                                .revenue(new BigDecimal("2000")).build()
                ))
                .topProducts(List.of(
                        YearlyDashboardResponseDTO.TopProductDTO.builder()
                                .productId(1L).productName("Popcorn")
                                .revenue(new BigDecimal("500")).build()
                ))
                .build();
    }
}
