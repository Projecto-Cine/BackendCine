package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponseDTO {
    private BigDecimal totalRevenue;
    private BigDecimal weeklyRevenue;
    private long totalPurchases;
    private long paidPurchases;
    private long activeScreenings;
    private long confirmedRoomBookings;
    private long totalUsers;
    private long activeMovies;
    private long unresolvedIncidents;
}
