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
    private LocalDateTime fechaHora;
    private BigDecimal precioBase;
    private int asientosDisponibles;
    private boolean completo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
