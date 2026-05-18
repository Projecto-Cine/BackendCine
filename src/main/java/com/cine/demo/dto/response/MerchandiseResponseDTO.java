package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MerchandiseResponseDTO(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        int stock,
        int minStock,
        String supplier,
        String imageUrl,
        boolean active,
        LocalDateTime createdAt
) {}
