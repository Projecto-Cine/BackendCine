package com.cine.demo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UserRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 2, message = "Name must have at least 2 characters")
        String name,
        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        @NotNull(message = "Birth date is required")
        LocalDate birthDate,
        String userType,
        Boolean student,
        Integer annualVisits,
        Boolean discountActive,
        String role
) {}
