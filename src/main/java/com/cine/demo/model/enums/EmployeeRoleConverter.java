package com.cine.demo.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EmployeeRoleConverter implements AttributeConverter<EmployeeRole, String> {

    @Override
    public String convertToDatabaseColumn(EmployeeRole role) {
        return role == null ? null : role.name().toLowerCase();
    }

    @Override
    public EmployeeRole convertToEntityAttribute(String value) {
        return EmployeeRole.from(value);
    }
}
