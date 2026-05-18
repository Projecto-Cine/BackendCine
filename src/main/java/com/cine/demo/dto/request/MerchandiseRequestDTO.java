package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MerchandiseRequestDTO(
        @NotBlank(message = "Name is required")
        String name,
        String description,
        String category,
        @NotNull(message = "Price is required")
        BigDecimal price,
        @PositiveOrZero(message = "Stock cannot be negative")
        int stock,
        String imageUrl,
        String emoji
) {}
