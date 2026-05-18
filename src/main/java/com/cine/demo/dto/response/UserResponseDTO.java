package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserResponseDTO(
        Long id,
        String name,
        String username,
        String lastName,
        String email,
        LocalDate birthDate,
        LocalDate dateOfBirth,
        String userType,
        boolean student,
        int annualVisits,
        int visitsPerYear,
        boolean discountActive,
        boolean fidelityDiscountEligible,
        String role,
        String status,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
