package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private int duracionMin;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    @NotBlank(message = "La clasificación de edad es obligatoria")
    private String clasificacionEdad;

    private String language;

    private String schedule;
}
