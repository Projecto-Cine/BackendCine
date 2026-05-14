package com.cine.demo.model.enums;

public enum EmployeeRole {
    CASHIER("CAJERO"),
    MANAGEMENT("GERENCIA"),
    CLEANING("LIMPIEZA"),
    MAINTENANCE("MANTENIMIENTO");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
