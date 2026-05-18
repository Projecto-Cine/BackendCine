package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private String category;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock cannot be negative")
    private int stock;

    @PositiveOrZero(message = "Min stock cannot be negative")
    private int minStock;

    private String supplier;

    private String imageUrl;

    private String emoji;
}
