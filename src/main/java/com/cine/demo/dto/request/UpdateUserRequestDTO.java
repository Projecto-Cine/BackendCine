package com.cine.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateUserRequestDTO(
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @Email(message = "Email must be a valid address")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        String userType,
        Boolean student,
        Integer yearlyVisits,
        String role
) {}
