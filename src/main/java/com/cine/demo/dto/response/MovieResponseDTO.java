package com.cine.demo.dto.response;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
