package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MerchandiseSaleRequestDTO(
        @NotNull(message = "User is required")
        Long userId,

        @NotNull(message = "Merchandise is required")
        Long merchandiseId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}
