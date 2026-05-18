package com.cine.demo.dto.request;

import com.cine.demo.model.enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateEmployeeRequestDTO {

    private String name;

    @Email(message = "Email format is invalid")
    private String email;

    private EmployeeRole role;

    private String phoneNumber;
}
