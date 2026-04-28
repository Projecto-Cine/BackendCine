package com.cine.demo.service.impl;

import com.cine.demo.dto.response.DashboardResponseDTO;
import com.cine.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardResponseDTO getDashboardData() { return null; }
}
