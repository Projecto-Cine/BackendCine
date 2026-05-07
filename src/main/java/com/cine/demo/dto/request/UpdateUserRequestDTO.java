package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
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

    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String name;

    private String lastName;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private String password;
    private LocalDate birthDate;
    private String userType;
    private Boolean student;
    private Integer visitsCurrentYear;
    private String role;
}