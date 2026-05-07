package com.cine.demo.controller;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.OccupancyResponseDTO;
import com.cine.demo.dto.response.SalesWeekResponseDTO;
import com.cine.demo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Informes", description = "Informes de ventas y ocupación")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-week")
    @Operation(summary = "Ventas de la semana actual agrupadas por día")
    public ResponseEntity<ApiResponse<List<SalesWeekResponseDTO>>> getSalesWeek() {
        return ResponseEntity.ok(ApiResponse.<List<SalesWeekResponseDTO>>builder()
                .success(true).message("Informe de ventas semanales obtenido correctamente")
                .data(reportService.getSalesWeek()).build());
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Ocupación de proyecciones agrupada por película")
    public ResponseEntity<ApiResponse<List<OccupancyResponseDTO>>> getOccupancy() {
        return ResponseEntity.ok(ApiResponse.<List<OccupancyResponseDTO>>builder()
                .success(true).message("Informe de ocupación obtenido correctamente")
                .data(reportService.getOccupancy()).build());
    }
}
