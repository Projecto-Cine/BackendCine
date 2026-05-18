package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class IncidentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String severity;
    private String category;
    private String room;
    private boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
