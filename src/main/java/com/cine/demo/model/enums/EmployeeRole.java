package com.cine.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmployeeRole {
    CASHIER("CAJERO"),
    MANAGEMENT("GERENCIA"),
    CLEANING("LIMPIEZA"),
    MAINTENANCE("MANTENIMIENTO");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static EmployeeRole from(String value) {
        if (value == null) return null;
        for (EmployeeRole role : values()) {
            if (role.name().equalsIgnoreCase(value) || role.displayName.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown employee role: " + value);
    }
}
