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
@Tag(name = "Reports", description = "Sales and occupancy reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales-week")
    @Operation(summary = "Current week sales grouped by day")
    public ResponseEntity<ApiResponse<List<SalesWeekResponseDTO>>> getSalesWeek() {
        return ResponseEntity.ok(ApiResponse.<List<SalesWeekResponseDTO>>builder()
                .success(true).message("Weekly sales report retrieved successfully")
                .data(reportService.getSalesWeek()).build());
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Screening occupancy grouped by movie")
    public ResponseEntity<ApiResponse<List<OccupancyResponseDTO>>> getOccupancy() {
        return ResponseEntity.ok(ApiResponse.<List<OccupancyResponseDTO>>builder()
                .success(true).message("Occupancy report retrieved successfully")
                .data(reportService.getOccupancy()).build());
    }
}
