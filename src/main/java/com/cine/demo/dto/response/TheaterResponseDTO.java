package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TheaterResponseDTO {
    private Long id;
    private String nombre;
    private int capacidad;
    private int totalSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
