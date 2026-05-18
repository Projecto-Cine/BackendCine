package com.cine.demo.dto.request;

import com.cine.demo.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.util.List;

@Builder
public record PurchaseRequestDTO(
        @NotNull(message = "User is required")
        Long userId,
        @NotNull(message = "Screening is required")
        Long screeningId,
        @NotEmpty(message = "Purchase must include at least one ticket")
        @Size(min = 1, message = "Purchase must include at least one ticket")
        @Valid
        List<TicketRequestDTO> tickets,
        PaymentMethod paymentMethod,
        @Email(message = "Guest email must be a valid email address")
        String guestEmail
) {}
