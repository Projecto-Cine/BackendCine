package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidentRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    @NotBlank(message = "La severidad es obligatoria")
    private String severity;
    private boolean resolved;
}
