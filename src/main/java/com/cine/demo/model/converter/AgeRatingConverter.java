package com.cine.demo.model.converter;

import com.cine.demo.model.enums.AgeRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AgeRatingConverter implements AttributeConverter<AgeRating, String> {

    @Override
    public String convertToDatabaseColumn(AgeRating attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public AgeRating convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AgeRating.fromDbValue(dbData);
    }
}
