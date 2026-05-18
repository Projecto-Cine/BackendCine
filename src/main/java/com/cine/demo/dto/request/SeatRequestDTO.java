package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SeatRequestDTO(
        @NotNull(message = "Theater ID is required")
        Long theaterId,
        @NotBlank(message = "Row is required")
        String row,
        @Min(value = 1, message = "Seat number must be at least 1")
        int number,
        @NotNull(message = "Seat type is required")
        String type
) {}
