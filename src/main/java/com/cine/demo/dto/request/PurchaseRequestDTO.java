package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record PurchaseRequestDTO(
        Long userId,
        Long screeningId,
        @NotEmpty(message = "Purchase must include at least one ticket")
        @Size(min = 1, message = "Purchase must include at least one ticket")
        @Valid
        List<TicketRequestDTO> tickets,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        @Email(message = "Guest email must be a valid email address")
        String guestEmail
) {}
