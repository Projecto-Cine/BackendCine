package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ScreeningResponseDTO {
    private Long id;
    private MovieResponseDTO movie;
    private TheaterResponseDTO theater;
    private LocalDateTime dateTime;
    private LocalDateTime endDatetime;
    private BigDecimal basePrice;
    private int availableSeats;
    private boolean full;
}