package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeRatingConverterTest {

    private final AgeRatingConverter converter = new AgeRatingConverter();

    /**
     * Comprueba que la conversión a la columna de la BD usa los códigos
     * cortos esperados (ALL, 7, 12, 16, 18) y NO el nombre del enum.
     */
    @Test
    void convertToDatabaseColumn_returnsShortCodes() {
        assertThat(converter.convertToDatabaseColumn(AgeRating.ALL)).isEqualTo("ALL");
        assertThat(converter.convertToDatabaseColumn(AgeRating.SEVEN)).isEqualTo("7");
        assertThat(converter.convertToDatabaseColumn(AgeRating.TWELVE)).isEqualTo("12");
        assertThat(converter.convertToDatabaseColumn(AgeRating.SIXTEEN)).isEqualTo("16");
        assertThat(converter.convertToDatabaseColumn(AgeRating.EIGHTEEN)).isEqualTo("18");
    }

    /**
     * Cuando la entidad tiene null, la columna también debe ser null.
     * Necesario para que JPA no rompa al insertar películas sin clasificación.
     */
    @Test
    void convertToDatabaseColumn_returnsNull_whenEnumIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    /**
     * Conversión inversa: la BD puede tener tanto códigos cortos
     * (7, 12, 16, 18) como nombres antiguos (SEVEN, TWELVE, etc.).
     * Ambos deben mapearse al enum correcto. Esto facilita migraciones.
     */
    @Test
    void convertToEntityAttribute_acceptsBothShortCodesAndEnumNames() {
        assertThat(converter.convertToEntityAttribute("ALL")).isEqualTo(AgeRating.ALL);
        assertThat(converter.convertToEntityAttribute("7")).isEqualTo(AgeRating.SEVEN);
        assertThat(converter.convertToEntityAttribute("SEVEN")).isEqualTo(AgeRating.SEVEN);
        assertThat(converter.convertToEntityAttribute("12")).isEqualTo(AgeRating.TWELVE);
        assertThat(converter.convertToEntityAttribute("TWELVE")).isEqualTo(AgeRating.TWELVE);
        assertThat(converter.convertToEntityAttribute("16")).isEqualTo(AgeRating.SIXTEEN);
        assertThat(converter.convertToEntityAttribute("SIXTEEN")).isEqualTo(AgeRating.SIXTEEN);
        assertThat(converter.convertToEntityAttribute("18")).isEqualTo(AgeRating.EIGHTEEN);
        assertThat(converter.convertToEntityAttribute("EIGHTEEN")).isEqualTo(AgeRating.EIGHTEEN);
    }

    /**
     * Si la columna es null, la entidad debe quedar con null sin lanzar.
     */
    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    /**
     * Si la BD trae un valor desconocido lanzamos IllegalArgumentException
     * porque preferimos un fallo claro a aceptar datos corruptos.
     */
    @Test
    void convertToEntityAttribute_throwsIllegalArgument_whenUnknownValue() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }
}
