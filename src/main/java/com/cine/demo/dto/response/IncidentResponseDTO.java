package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponseDTO {
    private Long id;
    private String title;
    private String category;
    private String priority;
    private String status;
    private String room;
    private String description;
    private String assignedTo;
    private String reportedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}