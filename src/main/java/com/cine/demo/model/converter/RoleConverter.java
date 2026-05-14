package com.cine.demo.model.converter;

import com.cine.demo.model.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) return null;
        if (role == Role.CLIENT) return "CLIENTE";
        return role.name();
    }

    @Override
    public Role convertToEntityAttribute(String value) {
        if (value == null) return null;
        if ("CLIENTE".equalsIgnoreCase(value) || "CLIENT".equalsIgnoreCase(value)) return Role.CLIENT;
        return Role.valueOf(value);
    }
}
