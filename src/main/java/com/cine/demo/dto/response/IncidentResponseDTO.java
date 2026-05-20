package com.cine.demo.dto.response;

import com.cine.demo.model.enums.IncidentStatus;
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
        IncidentStatus status,
        boolean resolved,
        AssignedEmployeeDTO assignedTo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}