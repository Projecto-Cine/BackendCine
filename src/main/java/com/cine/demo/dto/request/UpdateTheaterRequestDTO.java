package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateTheaterRequestDTO(
        @Size(max = 100, message = "Theater name must not exceed 100 characters")
        String name,

        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity
) {}
