package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateSeatRequestDTO(
        String row,
        @Min(value = 1, message = "Seat number must be at least 1")
        Integer number,
        String type
) {}
