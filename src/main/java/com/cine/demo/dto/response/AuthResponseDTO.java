package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDTO(
        String token,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String email,
        String role
) {}
