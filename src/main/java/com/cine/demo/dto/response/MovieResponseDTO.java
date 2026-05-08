package com.cine.demo.dto.response;

import com.cine.demo.model.enums.AgeRating;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MovieResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private int duracionMin;
    private String genero;
    private String clasificacionEdad;
    private String posterUrl;
    private boolean active;
    private String language;
    private String schedule;
    private LocalDateTime createdAt;
}
