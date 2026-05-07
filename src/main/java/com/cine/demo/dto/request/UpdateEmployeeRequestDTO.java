package com.cine.demo.dto.request;

import com.cine.demo.model.enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateEmployeeRequestDTO {

    private String name;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private EmployeeRole role;
}
