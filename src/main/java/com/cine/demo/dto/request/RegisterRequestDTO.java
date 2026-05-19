package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record RegisterRequestDTO(
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
        Boolean isStudent
) {}
