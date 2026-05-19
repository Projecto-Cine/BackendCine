package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record IncidentResponseDTO(
        Long id,
        String title,
        String description,
        String severity,
        String category,
        String room,
        boolean resolved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}