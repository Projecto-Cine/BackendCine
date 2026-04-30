package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class UpdateUserRequestDTO {

    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String nombre;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private String password;
    private LocalDate fechaNacimiento;
    private Boolean esEstudiante;
    private Integer visitasAnio;
    private String rol;
}
