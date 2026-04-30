package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatResponseDTO {
    private Long id;
    private Long theaterId;
    private String fila;
    private int numero;
    private String tipo;
}
