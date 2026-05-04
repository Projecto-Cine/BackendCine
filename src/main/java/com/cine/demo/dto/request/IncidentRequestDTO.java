package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String category;
    private String priority;
    private String status;
    private String room;
    private String description;
    private String assignedTo;

    @NotBlank(message = "El reportador es obligatorio")
    private String reportedBy;
}