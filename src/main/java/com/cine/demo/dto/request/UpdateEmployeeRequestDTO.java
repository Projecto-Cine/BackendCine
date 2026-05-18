package com.cine.demo.dto.request;

import com.cine.demo.model.enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UpdateEmployeeRequestDTO(
        String name,
        @Email(message = "Email format is invalid")
        String email,
        EmployeeRole role,
        String phoneNumber
) {}
