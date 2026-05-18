package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    private Long userId;

    @NotNull(message = "Screening is required")
    private Long screeningId;

    @Valid
    private List<TicketRequestDTO> tickets;

    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;

    @Email(message = "Guest email must be a valid email address")
    private String guestEmail;
}
