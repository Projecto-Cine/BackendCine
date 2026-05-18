package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record DashboardResponseDTO(
        BigDecimal totalRevenue,
        BigDecimal weeklyRevenue,
        long totalPurchases,
        long paidPurchases,
        long activeScreenings,
        long confirmedRoomBookings,
        long totalUsers,
        long activeMovies,
        long unresolvedIncidents
) {}
