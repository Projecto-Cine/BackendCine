package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record KpiResponseDTO(
        BigDecimal revenueToday,
        long ticketsToday,
        double occupancyAvg,
        long incidentsOpen,
        long activeSessions,
        long reservationsToday,
        long operationalRooms
) {}
