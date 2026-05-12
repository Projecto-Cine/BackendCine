package com.cine.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EmployeeRole {
    CAJERO, GERENCIA, SEGURIDAD, LIMPIEZA;

    @JsonCreator
    public static EmployeeRole from(String value) {
        if (value == null) return null;
        return EmployeeRole.valueOf(value.trim().toUpperCase());
    }
}
