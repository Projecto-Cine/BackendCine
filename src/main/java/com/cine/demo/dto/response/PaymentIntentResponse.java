package com.cine.demo.dto.response;

import lombok.Builder;

@Builder
public record PaymentIntentResponse(
        String clientSecret,
        String paymentIntentId,
        String publishableKey
) {}
