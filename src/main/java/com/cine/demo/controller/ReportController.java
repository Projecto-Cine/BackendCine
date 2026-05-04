package com.cine.demo.controller;

import com.cine.demo.dto.response.*;
import com.cine.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/kpis")
    public ResponseEntity<KpiResponseDTO> getKpis() {
        return ResponseEntity.ok(reportService.getKpis());
    }

    @GetMapping("/sales-week")
    public ResponseEntity<List<SalesWeekItemDTO>> getSalesWeek() {
        return ResponseEntity.ok(reportService.getSalesWeek());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<List<OccupancyItemDTO>> getOccupancy() {
        return ResponseEntity.ok(reportService.getOccupancy());
    }

    @GetMapping("/incidents-by-category")
    public ResponseEntity<List<CategoryReportDTO>> getIncidentsByCategory() {
        return ResponseEntity.ok(reportService.getIncidentsByCategory());
    }

    @GetMapping("/format-performance")
    public ResponseEntity<List<FormatPerformanceDTO>> getFormatPerformance() {
        return ResponseEntity.ok(reportService.getFormatPerformance());
    }
}