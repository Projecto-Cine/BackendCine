package com.cine.demo.dto.request;

import com.cine.demo.model.enums.MerchandiseCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MerchandiseRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Category is required")
        MerchandiseCategory category,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @PositiveOrZero(message = "Stock cannot be negative")
        int stock,

        String imageUrl
) {}
