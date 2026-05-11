package com.cine.demo.dto.request;

import com.cine.demo.model.enums.AgeRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String titulo;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String descripcion;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duracionMin;

    @NotBlank(message = "Genre is required")
    private String genero;

    @NotNull(message = "Age rating is required")
    private AgeRating clasificacionEdad;

    private String language;
    private String schedule;
}
