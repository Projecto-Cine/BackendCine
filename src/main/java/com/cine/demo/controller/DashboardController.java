package com.cine.demo.controller;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.dto.response.YearlyDashboardResponseDTO;
import com.cine.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Year;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Metrics and KPIs for the control panel")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get dashboard data", description = "Returns totals for sales, users, movies and other metrics")
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard retrieved successfully", dashboardService.getDashboardData()));
    }

    @GetMapping("/yearly")
    @Operation(summary = "Obtener datos anuales del dashboard", description = "Devuelve métricas anuales: ingresos, películas, sesiones, top 3 películas y productos")
    public ResponseEntity<ApiResponse<YearlyDashboardResponseDTO>> getYearlyData(
            @RequestParam(required = false) Integer year) {
        int targetYear = year != null ? year : Year.now().getValue();
        return ResponseEntity.ok(ApiResponse.ok("Datos anuales obtenidos correctamente", dashboardService.getYearlyData(targetYear)));
    }
}
