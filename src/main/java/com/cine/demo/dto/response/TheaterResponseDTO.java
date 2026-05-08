package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheaterResponseDTO {
    private Long id;
    private String nombre;
    private int capacidad;
    private int numRows;
    private int numColumns;
    private int totalSeats;
}
