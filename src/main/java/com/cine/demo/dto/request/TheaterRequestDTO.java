package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TheaterRequestDTO(
        @NotBlank(message = "Theater name is required")
        String name,
        @Min(value = 1, message = "Capacity must be at least 1")
        int capacity
) {}
