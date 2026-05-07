package com.cine.demo.dto.response;

import com.cine.demo.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private String token;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private Role role;
        private String imageUrl;
        private String status;
    }
}