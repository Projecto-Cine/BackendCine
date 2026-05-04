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
    private BigDecimal basePrice;
    private BigDecimal price;
    private int availableSeats;
    private String status;
    private boolean full;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}