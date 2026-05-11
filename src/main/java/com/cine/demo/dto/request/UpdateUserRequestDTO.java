package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String nombre;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Email must be a valid address")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Past(message = "Birth date must be in the past")
    private LocalDate fechaNacimiento;

    private String userType;
    private Boolean esEstudiante;
    private Integer visitasAnio;
    private String rol;
}
