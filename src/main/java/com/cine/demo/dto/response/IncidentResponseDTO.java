package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record IncidentResponseDTO(
        Long id,
        String title,
        String description,
        String severity,
        boolean resolved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
