package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Builder
public record TicketOfficeRequestDTO(
        @NotNull(message = "Screening is required")
        Long screeningId,
        @NotEmpty(message = "At least one seat must be selected")
        List<String> seats,
        @NotNull(message = "Ticket type is required")
        String ticketType,
        @NotNull(message = "Unit price is required")
        BigDecimal unitPrice,
        BigDecimal surcharge,
        @NotNull(message = "Total is required")
        BigDecimal total,
        @NotNull(message = "Payment method is required")
        String paymentMethod,
        @NotNull(message = "Cashier is required")
        Long cashierId,
        Long userId
) {}
