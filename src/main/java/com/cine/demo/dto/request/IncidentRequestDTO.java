package com.cine.demo.dto.request;

import com.cine.demo.model.enums.IncidentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record IncidentRequestDTO(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotBlank(message = "Severity is required")
        String severity,
        String category,
        String room,
        IncidentStatus status,
        Long assignedTo
) {}