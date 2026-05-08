package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchandiseSaleRequestDTO {

    private Long userId;

    private Long merchandiseId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int quantity;
}
