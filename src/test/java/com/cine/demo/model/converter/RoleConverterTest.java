package com.cine.demo.model.converter;

import com.cine.demo.model.enums.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleConverterTest {

    private final RoleConverter converter = new RoleConverter();

    @Test
    void convertToDatabaseColumn_returnsCliente_forClientRole() {
        assertThat(converter.convertToDatabaseColumn(Role.CLIENT)).isEqualTo("CLIENTE");
    }

    @Test
    void convertToDatabaseColumn_returnsEnumName_forOtherRoles() {
        assertThat(converter.convertToDatabaseColumn(Role.ADMIN)).isEqualTo("ADMIN");
        assertThat(converter.convertToDatabaseColumn(Role.SUPERVISOR)).isEqualTo("SUPERVISOR");
        assertThat(converter.convertToDatabaseColumn(Role.OPERATOR)).isEqualTo("OPERATOR");
    }

    @Test
    void convertToDatabaseColumn_returnsNull_whenRoleIsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_returnsClient_forCliente() {
        assertThat(converter.convertToEntityAttribute("CLIENTE")).isEqualTo(Role.CLIENT);
        assertThat(converter.convertToEntityAttribute("cliente")).isEqualTo(Role.CLIENT);
    }

    @Test
    void convertToEntityAttribute_returnsClient_forEnglishClient() {
        assertThat(converter.convertToEntityAttribute("CLIENT")).isEqualTo(Role.CLIENT);
        assertThat(converter.convertToEntityAttribute("client")).isEqualTo(Role.CLIENT);
    }

    @Test
    void convertToEntityAttribute_returnsOtherRoles_byEnumName() {
        assertThat(converter.convertToEntityAttribute("ADMIN")).isEqualTo(Role.ADMIN);
        assertThat(converter.convertToEntityAttribute("SUPERVISOR")).isEqualTo(Role.SUPERVISOR);
    }

    @Test
    void convertToEntityAttribute_returnsNull_whenDbValueIsNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_throwsException_whenUnknownValue() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("UNKNOWN_ROLE"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
