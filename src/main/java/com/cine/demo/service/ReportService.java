package com.cine.demo.service;

import com.cine.demo.dto.response.*;
import java.util.List;

public interface ReportService {
    KpiResponseDTO getKpis();
    List<SalesWeekItemDTO> getSalesWeek();
    List<OccupancyItemDTO> getOccupancy();
    List<CategoryReportDTO> getIncidentsByCategory();
    List<FormatPerformanceDTO> getFormatPerformance();
}