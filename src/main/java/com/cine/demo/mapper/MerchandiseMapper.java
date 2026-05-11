package com.cine.demo.mapper;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import org.springframework.stereotype.Component;

@Component
public class MerchandiseMapper {

    public Merchandise toEntity(MerchandiseRequestDTO dto) {
        return Merchandise.builder()
                .name(dto.name())
                .description(dto.description())
                .category(dto.category())
                .price(dto.price())
                .stock(dto.stock())
                .imageUrl(dto.imageUrl())
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
                .imageUrl(entity.getImageUrl())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(MerchandiseRequestDTO dto, Merchandise entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.category() != null) entity.setCategory(dto.category());
        if (dto.price() != null) entity.setPrice(dto.price());
        entity.setStock(dto.stock());
        if (dto.imageUrl() != null) entity.setImageUrl(dto.imageUrl());
    }
}
