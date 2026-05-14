package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePaymentIntentRequest {

    @NotNull
    private Long purchaseId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currency;
}