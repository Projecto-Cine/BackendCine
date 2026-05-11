package com.cine.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PurchaseRequestDTO(
        @NotNull(message = "User is required")
        Long userId,

        @NotNull(message = "Screening is required")
        Long screeningId,

        @NotEmpty(message = "Purchase must include at least one ticket")
        @Valid
        List<TicketRequestDTO> tickets
) {}
