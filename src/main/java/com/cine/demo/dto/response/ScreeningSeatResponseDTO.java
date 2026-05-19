package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record ScreeningSeatResponseDTO(
        Long id,
        Long screeningId,
        SeatResponseDTO seat,
        boolean occupied,
        /** "available" | "reserved" | "occupied" */
        String status
) {}
