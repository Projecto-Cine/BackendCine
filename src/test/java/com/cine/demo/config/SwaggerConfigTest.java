package com.cine.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    private final SwaggerConfig config = new SwaggerConfig();

    @Test
    void openAPI_isNotNull() {
        assertThat(config.openAPI()).isNotNull();
    }

    @Test
    void openAPI_hasCorrectTitle() {
        OpenAPI api = config.openAPI();
        assertThat(api.getInfo().getTitle()).isEqualTo("Cinema API");
    }

    @Test
    void openAPI_hasCorrectVersion() {
        OpenAPI api = config.openAPI();
        assertThat(api.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void openAPI_hasDescriptionSet() {
        OpenAPI api = config.openAPI();
        assertThat(api.getInfo().getDescription()).isNotBlank();
    }

    @Test
    void openAPI_hasBearerAuthSecurityScheme() {
        OpenAPI api = config.openAPI();
        SecurityScheme scheme = api.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");
    }

    @Test
    void openAPI_hasSecurityRequirement() {
        OpenAPI api = config.openAPI();
        assertThat(api.getSecurity()).isNotEmpty();
        assertThat(api.getSecurity().get(0).containsKey("bearerAuth")).isTrue();
    }
}
