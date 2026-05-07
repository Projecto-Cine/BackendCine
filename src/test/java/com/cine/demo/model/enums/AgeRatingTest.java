package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeRatingTest {

    /**
     * getValue() devuelve el código corto que viaja por JSON gracias a @JsonValue.
     * Esto permite que el frontend reciba "12" en vez de "TWELVE".
     */
    @Test
    void getValue_returnsShortCode() {
        assertThat(AgeRating.ALL.getValue()).isEqualTo("ALL");
        assertThat(AgeRating.SEVEN.getValue()).isEqualTo("7");
        assertThat(AgeRating.TWELVE.getValue()).isEqualTo("12");
        assertThat(AgeRating.SIXTEEN.getValue()).isEqualTo("16");
        assertThat(AgeRating.EIGHTEEN.getValue()).isEqualTo("18");
    }

    /**
     * fromValue() acepta TANTO el código corto ("7") COMO el nombre del enum
     * ("SEVEN"). Esto permite a Jackson deserializar JSONs que vienen de
     * sistemas externos que aún usan el formato antiguo.
     */
    @Test
    void fromValue_parsesShortCodes() {
        assertThat(AgeRating.fromValue("ALL")).isEqualTo(AgeRating.ALL);
        assertThat(AgeRating.fromValue("7")).isEqualTo(AgeRating.SEVEN);
        assertThat(AgeRating.fromValue("12")).isEqualTo(AgeRating.TWELVE);
        assertThat(AgeRating.fromValue("16")).isEqualTo(AgeRating.SIXTEEN);
        assertThat(AgeRating.fromValue("18")).isEqualTo(AgeRating.EIGHTEEN);
    }

    @Test
    void fromValue_parsesEnumNames() {
        assertThat(AgeRating.fromValue("SEVEN")).isEqualTo(AgeRating.SEVEN);
        assertThat(AgeRating.fromValue("TWELVE")).isEqualTo(AgeRating.TWELVE);
        assertThat(AgeRating.fromValue("EIGHTEEN")).isEqualTo(AgeRating.EIGHTEEN);
    }

    /**
     * fromValue con un valor desconocido lanza IllegalArgumentException.
     * Importante: queremos un fallo claro y no devolver null silenciosamente.
     */
    @Test
    void fromValue_throwsIllegalArgument_whenUnknown() {
        assertThatThrownBy(() -> AgeRating.fromValue("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }
}
