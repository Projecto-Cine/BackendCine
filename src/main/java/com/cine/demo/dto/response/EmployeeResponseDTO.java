package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record EmployeeResponseDTO(
        Long id,
        String name,
        String email,
        String role,
        String phoneNumber,
        LocalDateTime createdAt,
        boolean active
) {}
