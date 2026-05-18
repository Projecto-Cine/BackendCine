package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IncidentRequestDTO(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotBlank(message = "Severity is required")
        String severity,
        boolean resolved
) {}
