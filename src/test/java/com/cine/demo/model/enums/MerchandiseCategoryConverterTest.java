package com.cine.demo.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MerchandiseCategoryConverterTest {

    private final MerchandiseCategoryConverter converter = new MerchandiseCategoryConverter();

    /**
     * convertToDatabaseColumn: la columna debe almacenar el name() del enum.
     * Aquí cubrimos varias categorías para ejecutar todos los caminos del switch.
     */
    @Test
    void convertToDatabaseColumn_storesEnumName() {
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.CLOTHING)).isEqualTo("CLOTHING");
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.POSTERS)).isEqualTo("POSTERS");
        assertThat(converter.convertToDatabaseColumn(MerchandiseCategory.OTHER)).isEqualTo("OTHER");
    }

    /**
     * Si la categoría es null en la entidad, la columna también debe ser null
     * (no debe lanzar NullPointerException).
     */
    @Test
    void convertToDatabaseColumn_returnsNull_whenCategoryIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    /**
     * convertToEntityAttribute con valor exacto: lo convierte al enum correcto.
     */
    @Test
    void convertToEntityAttribute_returnsExactEnumMatch() {
        assertThat(converter.convertToEntityAttribute("CLOTHING")).isEqualTo(MerchandiseCategory.CLOTHING);
        assertThat(converter.convertToEntityAttribute("ACCESSORIES")).isEqualTo(MerchandiseCategory.ACCESSORIES);
        assertThat(converter.convertToEntityAttribute("FOOD")).isEqualTo(MerchandiseCategory.FOOD);
    }

    /**
     * El converter NORMALIZA: trim + uppercase.
     * Aceptamos "  food  " minúsculas/espacios y devolvemos FOOD.
     */
    @Test
    void convertToEntityAttribute_isCaseInsensitiveAndTrims() {
        assertThat(converter.convertToEntityAttribute("  food  ")).isEqualTo(MerchandiseCategory.FOOD);
        assertThat(converter.convertToEntityAttribute("Posters")).isEqualTo(MerchandiseCategory.POSTERS);
    }

    /**
     * Ante valor desconocido, en vez de fallar devolvemos OTHER como "fallback"
     * para que la app no se rompa con datos antiguos o corruptos.
     */
    @Test
    void convertToEntityAttribute_returnsOther_whenUnknown() {
        assertThat(converter.convertToEntityAttribute("XYZ_INVENTADO")).isEqualTo(MerchandiseCategory.OTHER);
    }

    /**
     * Si la columna trae null, devolvemos null (sin caer en el bucle).
     */
    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
