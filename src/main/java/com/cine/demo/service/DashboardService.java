package com.cine.demo.service;

import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.dto.response.YearlyDashboardResponseDTO;

public interface DashboardService {
    DashboardResponseDTO getDashboardData();
    YearlyDashboardResponseDTO getYearlyData(int year);
}
