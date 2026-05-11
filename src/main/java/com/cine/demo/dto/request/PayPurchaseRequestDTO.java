package com.cine.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPurchaseRequestDTO {

    private String paymentMethod;
    private String cardLastFour;
}
