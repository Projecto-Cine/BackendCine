package com.cine.demo.mapper;

import com.cine.demo.dto.request.MerchandiseRequestDTO;
import com.cine.demo.dto.response.MerchandiseResponseDTO;
import com.cine.demo.model.Merchandise;
import org.springframework.stereotype.Component;

@Component
public class MerchandiseMapper {

    public Merchandise toEntity(MerchandiseRequestDTO dto) {
        return Merchandise.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stock(dto.getStock())
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
                .imageUrl(entity.getImageUrl())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntityFromDto(MerchandiseRequestDTO dto, Merchandise entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        if (dto.getImageUrl() != null) entity.setImageUrl(dto.getImageUrl());
    }
}
