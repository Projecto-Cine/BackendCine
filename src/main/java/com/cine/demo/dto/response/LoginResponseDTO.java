package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String token,
        UserInfo user
) {
    @Builder
    public record UserInfo(
            Long id,
            String name,
            String email,
            String role,
            String imageUrl,
            String status
    ) {}
}
