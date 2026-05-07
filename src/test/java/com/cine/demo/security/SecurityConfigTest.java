package com.cine.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void jwtAuthenticationFilter_isCreated() {
        SecurityConfig config = new SecurityConfig(mock(JwtUtil.class));

        JwtAuthenticationFilter filter = config.jwtAuthenticationFilter();

        assertThat(filter).isNotNull();
    }

    @Test
    void filterRegistration_appliesToApiPaths_withCorrectOrder() {
        SecurityConfig config = new SecurityConfig(mock(JwtUtil.class));
        JwtAuthenticationFilter filter = config.jwtAuthenticationFilter();

        FilterRegistrationBean<JwtAuthenticationFilter> registration = config.jwtFilterRegistration(filter);

        assertThat(registration.getFilter()).isSameAs(filter);
        assertThat(registration.getUrlPatterns()).containsExactly("/api/*");
        assertThat(registration.getOrder()).isEqualTo(1);
        assertThat(registration.getFilterName()).isEqualTo("jwtAuthenticationFilter");
    }
}
