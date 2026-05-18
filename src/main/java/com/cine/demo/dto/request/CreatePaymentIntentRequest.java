package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record CreatePaymentIntentRequest(
        @NotNull
        Long purchaseId,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        String currency
) {}
