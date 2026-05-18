package com.cine.demo.mapper;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import com.cine.demo.model.enums.MerchandiseCategory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MerchandiseMapper {

    private static final Map<String, MerchandiseCategory> CATEGORY_MAP = Map.of(
            "palomitas", MerchandiseCategory.FOOD,
            "bebidas",   MerchandiseCategory.DRINK,
            "snacks",    MerchandiseCategory.FOOD,
            "combos",    MerchandiseCategory.FOOD,
            "concesión", MerchandiseCategory.MERCHANDISE,
            "concesion", MerchandiseCategory.MERCHANDISE,
            "food",      MerchandiseCategory.FOOD,
            "drink",     MerchandiseCategory.DRINK,
            "merchandise", MerchandiseCategory.MERCHANDISE
    );

    private MerchandiseCategory parseCategory(String raw) {
        if (raw == null) return MerchandiseCategory.OTHER;
        String key = raw.trim().toLowerCase();
        MerchandiseCategory mapped = CATEGORY_MAP.get(key);
        if (mapped != null) return mapped;
        try {
            return MerchandiseCategory.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MerchandiseCategory.OTHER;
        }
    }

    public Merchandise toEntity(MerchandiseRequestDTO dto) {
        return Merchandise.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(parseCategory(dto.getCategory()))
                .price(dto.getPrice())
                .stock(dto.getStock())
                .minStock(dto.getMinStock())
                .supplier(dto.getSupplier())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    public MerchandiseResponseDTO toResponseDto(Merchandise entity) {
        return MerchandiseResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory() != null ? entity.getCategory().name() : null)
                .price(entity.getPrice())
                .stock(entity.getStock())
                .minStock(entity.getMinStock())
                .supplier(entity.getSupplier())
                .imageUrl(entity.getImageUrl())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(MerchandiseRequestDTO dto, Merchandise entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getCategory() != null) entity.setCategory(parseCategory(dto.getCategory()));
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setMinStock(dto.getMinStock());
        if (dto.getSupplier() != null) entity.setSupplier(dto.getSupplier());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) entity.setImageUrl(dto.getImageUrl());
    }
}
