package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SeatRequestDTO(
        @NotNull(message = "Theater ID is required")
        Long theaterId,

        @NotBlank(message = "Row is required")
        @Size(max = 5, message = "Row must not exceed 5 characters")
        String row,

        @Min(value = 1, message = "Seat number must be at least 1")
        int number,

        @NotNull(message = "Seat type is required")
        String type
) {}
