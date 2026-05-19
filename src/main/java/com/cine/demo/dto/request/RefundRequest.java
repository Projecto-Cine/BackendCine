package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RefundRequest(
        @NotNull
        Long purchaseId,
        String reason
) {}
