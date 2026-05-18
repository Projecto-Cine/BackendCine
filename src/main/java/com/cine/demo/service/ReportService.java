package com.cine.demo.service;

import com.cine.demo.dto.response.OccupancyResponseDTO;
import com.cine.demo.dto.response.SalesWeekResponseDTO;
import java.util.List;

public interface ReportService {
    List<SalesWeekResponseDTO> getSalesWeek();
    List<OccupancyResponseDTO> getOccupancy();
}
