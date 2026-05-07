package com.cine.demo.controller;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Métricas y KPIs para el panel de control")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Obtener datos del dashboard", description = "Devuelve totales de ventas, usuarios, películas y otras métricas")
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.<DashboardResponseDTO>builder()
                .success(true).message("Dashboard obtenido correctamente")
                .data(dashboardService.getDashboardData()).build());
    }
}
