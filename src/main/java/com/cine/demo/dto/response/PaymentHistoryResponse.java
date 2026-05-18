package com.cine.demo.dto.response;

import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentHistoryResponse(
        Long purchaseId,
        String paymentIntentId,
        BigDecimal amount,
        PurchaseStatus status,
        PaymentMethod paymentMethod,
        String type,
        LocalDateTime createdAt,
        Long userId,
        String userName
) {}
