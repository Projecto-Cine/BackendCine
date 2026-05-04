package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TheaterResponseDTO {
    private Long id;
    private String name;
    private int capacity;
    private int totalSeats;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}