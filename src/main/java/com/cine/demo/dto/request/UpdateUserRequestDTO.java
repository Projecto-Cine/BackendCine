package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UpdateUserRequestDTO(
        @Size(min = 2, message = "Name must have at least 2 characters")
        String name,
        String lastName,
        @Email(message = "Email format is invalid")
        String email,
        String password,
        LocalDate birthDate,
        String userType,
        Boolean student,
        Integer annualVisits,
        Boolean discountActive,
        String role
) {}
