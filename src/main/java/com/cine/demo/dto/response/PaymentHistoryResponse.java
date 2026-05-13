package com.cine.demo.dto.response;

import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentHistoryResponse {
    private Long purchaseId;
    private String paymentIntentId;
    private BigDecimal amount;
    private PurchaseStatus status;
    private PaymentMethod paymentMethod;
    private String type;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
}