package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record RefundResponse(
        String refundId,
        BigDecimal amount,
        String status
) {}
