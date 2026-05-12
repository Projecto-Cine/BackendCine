package com.cine.demo.mapper;

import com.cine.demo.dto.response.MerchandiseSaleResponseDTO;
import com.cine.demo.model.MerchandiseSale;
import org.springframework.stereotype.Component;

@Component
public class MerchandiseSaleMapper {

    public MerchandiseSaleResponseDTO toResponseDto(MerchandiseSale entity) {
        return MerchandiseSaleResponseDTO.builder()
                .id(entity.getId())
                .purchaseId(entity.getPurchase() != null ? entity.getPurchase().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .merchandiseId(entity.getMerchandise() != null ? entity.getMerchandise().getId() : null)
                .merchandiseName(entity.getMerchandise() != null ? entity.getMerchandise().getName() : null)
                .quantity(entity.getQuantity())
                .total(entity.getTotal())
                .saleDate(entity.getSaleDate())
                .build();
    }
}
