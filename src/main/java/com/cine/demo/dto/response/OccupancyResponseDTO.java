package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record OccupancyResponseDTO(
        Long screeningId,
        String movieTitle,
        String theaterName,
        LocalDateTime startDatetime,
        int totalSeats,
        int occupiedSeats,
        double occupancyPercentage
) {}
