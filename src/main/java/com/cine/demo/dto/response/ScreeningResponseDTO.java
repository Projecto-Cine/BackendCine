package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ScreeningResponseDTO(
        Long id,
        MovieResponseDTO movie,
        TheaterResponseDTO theater,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        BigDecimal basePrice,
        int availableSeats,
        boolean full
) {}
