package com.cine.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsConfigTest {

    private static CorsConfiguration configFor(String originsCsv) {
        CorsConfigurationSource source = new CorsConfig(originsCsv).corsConfigurationSource();
        return ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    }

    @Test
    void parsesCsvIntoExactOrigins() {
        CorsConfiguration config = configFor("https://lumen.app,http://localhost:5173");

        assertThat(config.getAllowedOrigins())
                .containsExactly("https://lumen.app", "http://localhost:5173");
    }

    @Test
    void trimsWhitespaceAndIgnoresEmptyEntries() {
        CorsConfiguration config = configFor("  https://a.app ,, http://localhost:5173  ,");

        assertThat(config.getAllowedOrigins())
                .containsExactly("https://a.app", "http://localhost:5173");
    }

    /**
     * El fallo que motivo el cambio: con setAllowedOriginPatterns("*") y
     * allowCredentials(true), Spring refleja cualquier Origin que pida el
     * navegador. Nunca debe haber patrones comodin.
     */
    @Test
    void neverUsesWildcardPatterns() {
        CorsConfiguration config = configFor("https://lumen.app");

        assertThat(config.getAllowedOriginPatterns()).isNullOrEmpty();
        assertThat(config.getAllowedOrigins()).doesNotContain("*");
    }

    @Test
    void allowsCredentialsOnlyAlongsideExactOrigins() {
        CorsConfiguration config = configFor("https://lumen.app");

        assertThat(config.getAllowCredentials()).isTrue();
        assertThat(config.getAllowedOrigins()).allSatisfy(origin ->
                assertThat(origin).doesNotContain("*"));
    }

    @Test
    void rejectsWildcardOrigin() {
        assertThatThrownBy(() -> new CorsConfig("*"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("*");
    }

    @Test
    void rejectsWildcardMixedWithRealOrigins() {
        assertThatThrownBy(() -> new CorsConfig("https://lumen.app,*"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsEmptyOriginList() {
        assertThatThrownBy(() -> new CorsConfig("  ,, "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("vacio");
    }

    @Test
    void exposesOnlyTheHeadersTheApiActuallyUses() {
        CorsConfiguration config = configFor("https://lumen.app");

        assertThat(config.getAllowedHeaders())
                .containsExactlyInAnyOrder("Authorization", "Content-Type", "Accept", "Stripe-Signature")
                .doesNotContain("*");
    }

    @Test
    void appliesToEveryPath() {
        CorsConfigurationSource source = new CorsConfig("https://lumen.app").corsConfigurationSource();

        assertThat(((UrlBasedCorsConfigurationSource) source).getCorsConfigurations())
                .containsKey("/**");
    }

    @Test
    void buildsCorsFilterFromTheConfiguredSource() {
        CorsConfig config = new CorsConfig("https://lumen.app");

        assertThat(config.corsFilter())
                .isInstanceOf(CorsFilter.class);
    }
}
