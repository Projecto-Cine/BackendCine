package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReservationRequestDTO {

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Client email is required")
    private String clientEmail;

    @NotNull(message = "Screening is required")
    private Long screeningId;

    private PaymentMethod paymentMethod;

    private PurchaseStatus status;

    private BigDecimal totalAmount;
}