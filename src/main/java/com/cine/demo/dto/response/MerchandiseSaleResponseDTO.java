package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MerchandiseSaleResponseDTO(
        Long id,
        Long userId,
        Long merchandiseId,
        String merchandiseName,
        int quantity,
        BigDecimal total,
        LocalDateTime saleDate
) {}
