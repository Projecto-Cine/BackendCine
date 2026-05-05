package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KpiResponseDTO {
    private double revenueToday;
    private int ticketsToday;
    private double occupancyAvg;
    private int incidentsOpen;
    private int activeSessions;
    private int reservationsToday;
    private int operationalRooms;
    private int totalClients;
}