package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserResponseDTO(
        Long id,
        String name,
        String lastName,
        String email,
        LocalDate birthDate,
        String userType,
        boolean student,
        int yearlyVisits,
        boolean discountActive,
        String role,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
