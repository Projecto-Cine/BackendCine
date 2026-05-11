package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MerchandiseCategoryConverterTest {

    private final MerchandiseCategoryConverter converter = new MerchandiseCategoryConverter();

    @Test
    void convertToDatabaseColumn_storesEnumName() {
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.CLOTHING)).isEqualTo("CLOTHING");
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.POSTERS)).isEqualTo("POSTERS");
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.OTHER)).isEqualTo("OTHER");
    }

    @Test
    void convertToDatabaseColumn_returnsNull_whenCategoryIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_returnsExactEnumMatch() {
        assertThat(converter.convertToEntityAttribute("CLOTHING")).isEqualTo(MerchandiseCategory.CLOTHING);
        assertThat(converter.convertToEntityAttribute("ACCESSORIES")).isEqualTo(MerchandiseCategory.ACCESSORIES);
        assertThat(converter.convertToEntityAttribute("FOOD")).isEqualTo(MerchandiseCategory.FOOD);
    }

    @Test
    void convertToEntityAttribute_isCaseInsensitiveAndTrims() {
        assertThat(converter.convertToEntityAttribute("  food  ")).isEqualTo(MerchandiseCategory.FOOD);
        assertThat(converter.convertToEntityAttribute("Posters")).isEqualTo(MerchandiseCategory.POSTERS);
    }

    @Test
    void convertToEntityAttribute_returnsOther_whenUnknown() {
        assertThat(converter.convertToEntityAttribute("XYZ_INVENTADO")).isEqualTo(MerchandiseCategory.OTHER);
    }

    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
