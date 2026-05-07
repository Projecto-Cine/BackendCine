package com.cine.demo.security;

import com.cine.demo.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticatedUser {
    private final Long id;
    private final String email;
    private final Role role;
}
