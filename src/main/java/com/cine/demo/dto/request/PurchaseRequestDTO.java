package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record PurchaseRequestDTO(
        Long userId,
        Long screeningId,
        @Valid
        List<TicketRequestDTO> tickets,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        @Email(message = "Guest email must be a valid email address")
        String guestEmail
) {}
