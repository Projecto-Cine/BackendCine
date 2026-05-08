package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String nombre;
    private String lastName;
    private String email;
    private LocalDate fechaNacimiento;
    private String userType;
    private boolean esEstudiante;
    private int visitasAnio;
    private boolean discountActive;
    private String rol;
    private String imagenUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
