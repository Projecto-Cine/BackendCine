package com.cine.demo.security;

import lombok.Builder;

@Builder
public record AuthenticatedUser(
        Long id,
        String email,
        String role
) {}
