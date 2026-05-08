package com.cine.demo.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AgeRatingConverter implements AttributeConverter<AgeRating, String> {

    @Override
    public String convertToDatabaseColumn(AgeRating ageRating) {
        if (ageRating == null) return null;
        return switch (ageRating) {
            case ALL -> "ALL";
            case SEVEN -> "7";
            case TWELVE -> "12";
            case SIXTEEN -> "16";
            case EIGHTEEN -> "18";
        };
    }

    @Override
    public AgeRating convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "ALL" -> AgeRating.ALL;
            case "7", "SEVEN" -> AgeRating.SEVEN;
            case "12", "TWELVE" -> AgeRating.TWELVE;
            case "16", "SIXTEEN" -> AgeRating.SIXTEEN;
            case "18", "EIGHTEEN" -> AgeRating.EIGHTEEN;
            default -> throw new IllegalArgumentException("Unknown AgeRating: " + dbData);
        };
    }
}
