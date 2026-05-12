package com.cine.demo.dto.request;

import com.cine.demo.model.enums.MerchandiseCategory;
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

    @NotNull(message = "Category is required")
    private MerchandiseCategory category;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock cannot be negative")
    private int stock;

    private String imageUrl;
}
