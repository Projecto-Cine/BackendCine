package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateTheaterRequestDTO(
        String name,
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity
) {}
