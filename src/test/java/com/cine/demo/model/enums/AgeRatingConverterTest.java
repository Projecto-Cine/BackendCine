package com.cine.demo.model.enums;

import com.cine.demo.model.converter.AgeRatingConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeRatingConverterTest {

    private final AgeRatingConverter converter = new AgeRatingConverter();

    @Test
    void convertToDatabaseColumn_returnsShortCodes() {
        assertThat(converter.convertToDatabaseColumn(AgeRating.ALL)).isEqualTo("ALL");
        assertThat(converter.convertToDatabaseColumn(AgeRating.SEVEN)).isEqualTo("7");
        assertThat(converter.convertToDatabaseColumn(AgeRating.TWELVE)).isEqualTo("12");
        assertThat(converter.convertToDatabaseColumn(AgeRating.SIXTEEN)).isEqualTo("16");
        assertThat(converter.convertToDatabaseColumn(AgeRating.EIGHTEEN)).isEqualTo("18");
    }

    @Test
    void convertToDatabaseColumn_returnsNull_whenEnumIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_acceptsShortCodes() {
        assertThat(converter.convertToEntityAttribute("ALL")).isEqualTo(AgeRating.ALL);
        assertThat(converter.convertToEntityAttribute("7")).isEqualTo(AgeRating.SEVEN);
        assertThat(converter.convertToEntityAttribute("12")).isEqualTo(AgeRating.TWELVE);
        assertThat(converter.convertToEntityAttribute("16")).isEqualTo(AgeRating.SIXTEEN);
        assertThat(converter.convertToEntityAttribute("18")).isEqualTo(AgeRating.EIGHTEEN);
    }

    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_throwsIllegalArgument_whenUnknownValue() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }
}
