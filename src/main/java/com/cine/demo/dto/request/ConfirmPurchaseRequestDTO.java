package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import lombok.Builder;

@Builder
public record ConfirmPurchaseRequestDTO(
        PaymentMethod paymentMethod
) {}
