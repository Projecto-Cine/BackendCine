package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheaterResponseDTO {
    private Long id;
    private String name;
    private int capacity;
    private int numRows;
    private int numColumns;
    private int totalSeats;
}