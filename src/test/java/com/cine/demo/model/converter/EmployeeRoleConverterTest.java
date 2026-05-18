package com.cine.demo.model.converter;

import com.cine.demo.model.enums.EmployeeRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeRoleConverterTest {

    private final EmployeeRoleConverter converter = new EmployeeRoleConverter();

    @Test
    void convertToDatabaseColumn_returnsSpanishDisplayName() {
        assertThat(converter.convertToDatabaseColumn(EmployeeRole.CASHIER)).isEqualTo("CAJERO");
        assertThat(converter.convertToDatabaseColumn(EmployeeRole.MANAGEMENT)).isEqualTo("GERENCIA");
        assertThat(converter.convertToDatabaseColumn(EmployeeRole.CLEANING)).isEqualTo("LIMPIEZA");
        assertThat(converter.convertToDatabaseColumn(EmployeeRole.MAINTENANCE)).isEqualTo("MANTENIMIENTO");
    }

    @Test
    void convertToDatabaseColumn_returnsNull_whenRoleIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_acceptsSpanishDisplayNames() {
        assertThat(converter.convertToEntityAttribute("CAJERO")).isEqualTo(EmployeeRole.CASHIER);
        assertThat(converter.convertToEntityAttribute("GERENCIA")).isEqualTo(EmployeeRole.MANAGEMENT);
        assertThat(converter.convertToEntityAttribute("LIMPIEZA")).isEqualTo(EmployeeRole.CLEANING);
        assertThat(converter.convertToEntityAttribute("MANTENIMIENTO")).isEqualTo(EmployeeRole.MAINTENANCE);
    }

    @Test
    void convertToEntityAttribute_acceptsEnglishEnumNames() {
        assertThat(converter.convertToEntityAttribute("CASHIER")).isEqualTo(EmployeeRole.CASHIER);
        assertThat(converter.convertToEntityAttribute("MANAGEMENT")).isEqualTo(EmployeeRole.MANAGEMENT);
        assertThat(converter.convertToEntityAttribute("CLEANING")).isEqualTo(EmployeeRole.CLEANING);
        assertThat(converter.convertToEntityAttribute("MAINTENANCE")).isEqualTo(EmployeeRole.MAINTENANCE);
    }

    @Test
    void convertToEntityAttribute_isCaseInsensitive() {
        assertThat(converter.convertToEntityAttribute("cajero")).isEqualTo(EmployeeRole.CASHIER);
        assertThat(converter.convertToEntityAttribute("gerencia")).isEqualTo(EmployeeRole.MANAGEMENT);
        assertThat(converter.convertToEntityAttribute("cashier")).isEqualTo(EmployeeRole.CASHIER);
    }

    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_throwsIllegalArgument_whenUnknownValue() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UNKNOWN");
    }
}
