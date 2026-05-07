package com.cine.demo.model.enums;

public enum AgeRating {
    ALL("ALL"), SEVEN("7"), TWELVE("12"), SIXTEEN("16"), EIGHTEEN("18");

    private final String dbValue;

    AgeRating(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static AgeRating fromDbValue(String dbValue) {
        for (AgeRating r : values()) {
            if (r.dbValue.equals(dbValue)) return r;
        }
        throw new IllegalArgumentException("Valor de clasificación de edad no reconocido: " + dbValue);
    }
}
