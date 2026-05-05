package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMovieRequestDTO {
    private String titulo;
    private String descripcion;

    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer duracionMin;

    private String genero;
    private String clasificacionEdad;
    private String language;
    private String schedule;
}
