package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidentRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Severity is required")
    private String severity;
    private String category;
    private String room;
    private boolean resolved;
}
