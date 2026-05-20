package com.cine.demo.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MerchandiseCategoryConverter implements AttributeConverter<MerchandiseCategory, String> {

    @Override
    public String convertToDatabaseColumn(MerchandiseCategory category) {
        if (category == null) return null;
        return category.name();
    }

    @Override
    public MerchandiseCategory convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String normalized = dbData.trim().toUpperCase();
        for (MerchandiseCategory cat : MerchandiseCategory.values()) {
            if (cat.name().equals(normalized)) {
                return cat;
            }
        }
        return MerchandiseCategory.OTHER;
    }
}
