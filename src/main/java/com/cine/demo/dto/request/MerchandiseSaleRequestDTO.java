package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchandiseSaleRequestDTO {

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Merchandise is required")
    private Long merchandiseId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
