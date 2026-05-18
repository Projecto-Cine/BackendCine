package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record MerchandiseSaleRequestDTO(
        Long userId,
        Long merchandiseId,
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,
        List<Item> items,
        BigDecimal total,
        String paymentMethod,
        String payment_method
) {
    @Builder
    public record Item(
            Long merchandiseId,
            Long productId,
            Long product_id,
            @Min(value = 1, message = "La cantidad debe ser al menos 1")
            int quantity,
            int qty
    ) {}
}
