package com.cine.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgeRating {
    ALL("ALL"),
    SEVEN("7"),
    TWELVE("12"),
    SIXTEEN("16"),
    EIGHTEEN("18");

    private final String value;

    AgeRating(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AgeRating fromValue(String value) {
        for (AgeRating rating : values()) {
            if (rating.value.equals(value) || rating.name().equals(value)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid AgeRating value: " + value);
    }
}
