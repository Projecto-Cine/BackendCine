package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record TheaterResponseDTO(
        Long id,
        String name,
        int capacity,
        int numRows,
        int numColumns,
        int totalSeats
) {}
