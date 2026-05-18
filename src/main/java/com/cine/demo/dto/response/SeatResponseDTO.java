package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record SeatResponseDTO(
        Long id,
        Long theaterId,
        String row,
        int number,
        String type
) {}
