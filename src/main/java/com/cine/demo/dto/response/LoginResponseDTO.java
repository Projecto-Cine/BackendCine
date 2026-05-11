package com.cine.demo.dto.response;

import com.cine.demo.model.enums.Role;
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
            Role role,
            String imageUrl,
            String status
    ) {}
}
