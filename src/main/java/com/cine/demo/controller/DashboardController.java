package com.cine.demo.controller;

import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponseDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.<DashboardResponseDTO>builder()
                .success(true)
                .message("Dashboard obtenido correctamente")
                .data(dashboardService.getDashboardData())
                .build());
    }
}
