package com.cine.demo.dto.request;

import com.cine.demo.model.enums.MerchandiseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private MerchandiseCategory category;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @PositiveOrZero(message = "El stock no puede ser negativo")
    private int stock;

    private String imageUrl;
}