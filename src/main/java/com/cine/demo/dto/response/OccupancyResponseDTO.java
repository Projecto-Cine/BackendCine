package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OccupancyResponseDTO {
    private Long screeningId;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime dateTime;
    private int totalSeats;
    private int occupiedSeats;
    private double occupancyPercentage;
}