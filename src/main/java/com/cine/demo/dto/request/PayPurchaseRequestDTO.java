package com.cine.demo.dto.request;

import lombok.Builder;

@Builder
public record PayPurchaseRequestDTO(
        String paymentMethod,
        String cardLastFour
) {}
