package com.cine.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void securityConfig_hasEnableMethodSecurity() {
        assertThat(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class)).isTrue();
    }

    @Test
    void securityConfig_canBeInstantiated() {
        SecurityConfig config = new SecurityConfig(null);
        assertThat(config).isNotNull();
    }
}
