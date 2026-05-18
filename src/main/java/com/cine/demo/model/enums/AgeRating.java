package com.cine.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
        throw new IllegalArgumentException("Unrecognized age rating value: " + dbValue);
    }
}
