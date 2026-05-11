package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class KpiResponseDTO {
    private BigDecimal revenueToday;
    private long ticketsToday;
    private double occupancyAvg;
    private long incidentsOpen;
    private long activeSessions;
    private long reservationsToday;
    private long operationalRooms;
}
