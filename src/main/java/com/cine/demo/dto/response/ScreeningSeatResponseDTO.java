package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScreeningSeatResponseDTO {
    private Long id;
    private Long screeningId;
    private SeatResponseDTO seat;
    private boolean occupied;
    /** "available" | "reserved" | "occupied" */
    private String status;
}
