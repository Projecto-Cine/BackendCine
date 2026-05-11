package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateSeatRequestDTO(
        @Size(max = 5, message = "Row must not exceed 5 characters")
        String row,

        @Min(value = 1, message = "Seat number must be at least 1")
        Integer number,

        String type
) {}
