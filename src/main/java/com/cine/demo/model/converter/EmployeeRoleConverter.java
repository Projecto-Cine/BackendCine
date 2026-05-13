package com.cine.demo.model.converter;

import com.cine.demo.model.enums.EmployeeRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class EmployeeRoleConverter implements AttributeConverter<EmployeeRole, String> {

    private static final Map<EmployeeRole, String> TO_DB = Map.of(
            EmployeeRole.CASHIER,    "CAJERO",
            EmployeeRole.MANAGEMENT, "GERENCIA",
            EmployeeRole.SECURITY,   "SEGURIDAD",
            EmployeeRole.CLEANING,   "LIMPIEZA"
    );

    private static final Map<String, EmployeeRole> FROM_DB = Map.of(
            "CAJERO",    EmployeeRole.CASHIER,
            "GERENCIA",  EmployeeRole.MANAGEMENT,
            "SEGURIDAD", EmployeeRole.SECURITY,
            "LIMPIEZA",  EmployeeRole.CLEANING,
            "CASHIER",   EmployeeRole.CASHIER,
            "MANAGEMENT",EmployeeRole.MANAGEMENT,
            "SECURITY",  EmployeeRole.SECURITY,
            "CLEANING",  EmployeeRole.CLEANING
    );

    @Override
    public String convertToDatabaseColumn(EmployeeRole role) {
        return role == null ? null : TO_DB.getOrDefault(role, role.name());
    }

    @Override
    public EmployeeRole convertToEntityAttribute(String value) {
        if (value == null) return null;
        EmployeeRole result = FROM_DB.get(value.toUpperCase());
        if (result == null) throw new IllegalArgumentException("Unknown EmployeeRole value: " + value);
        return result;
    }
}
