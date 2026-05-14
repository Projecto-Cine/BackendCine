package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundRequest {

    @NotNull
    private Long purchaseId;

    private String reason;
}