package com.cine.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private final CorsConfig config = new CorsConfig();

    @Test
    void corsFilter_isNotNull() {
        assertThat(config.corsFilter()).isNotNull();
    }

    @Test
    void corsFilter_returnsCorsFilterInstance() {
        assertThat(config.corsFilter()).isInstanceOf(CorsFilter.class);
    }
}
