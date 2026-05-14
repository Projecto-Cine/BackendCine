package com.cine.demo.report;

import com.cine.demo.controller.ReportController;
import com.cine.demo.dto.response.OccupancyResponseDTO;
import com.cine.demo.dto.response.SalesWeekResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import(GlobalExceptionHandler.class)
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ReportService reportService;

    @Test
    void getSalesWeek_returns200WithWeeklyData() throws Exception {
        SalesWeekResponseDTO day = SalesWeekResponseDTO.builder()
                .date(LocalDate.of(2026, 5, 13))
                .totalPurchases(5)
                .revenue(new BigDecimal("250.00"))
                .build();
        when(reportService.getSalesWeek()).thenReturn(List.of(day));

        mockMvc.perform(get("/api/reports/sales-week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Weekly sales report retrieved successfully"))
                .andExpect(jsonPath("$.data[0].totalPurchases").value(5))
                .andExpect(jsonPath("$.data[0].revenue").value(250.00));
    }

    @Test
    void getSalesWeek_returns200WithEmptyList_whenNoSales() throws Exception {
        when(reportService.getSalesWeek()).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/sales-week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getOccupancy_returns200WithOccupancyData() throws Exception {
        OccupancyResponseDTO occupancy = OccupancyResponseDTO.builder()
                .screeningId(1L).movieTitle("Inception").theaterName("Hall A")
                .startTime(LocalDateTime.of(2026, 5, 13, 20, 0))
                .totalSeats(100).occupiedSeats(75).occupancyPercentage(75.0)
                .build();
        when(reportService.getOccupancy()).thenReturn(List.of(occupancy));

        mockMvc.perform(get("/api/reports/occupancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Occupancy report retrieved successfully"))
                .andExpect(jsonPath("$.data[0].movieTitle").value("Inception"))
                .andExpect(jsonPath("$.data[0].occupancyPercentage").value(75.0));
    }

    @Test
    void getOccupancy_returns200WithEmptyList_whenNoScreenings() throws Exception {
        when(reportService.getOccupancy()).thenReturn(List.of());

        mockMvc.perform(get("/api/reports/occupancy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
