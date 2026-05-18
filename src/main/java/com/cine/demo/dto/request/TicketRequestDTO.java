package com.cine.demo.dto.request;

import com.cine.demo.model.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TicketRequestDTO(
        @NotNull(message = "Seat is required")
        Long screeningSeatId,
        @NotNull(message = "Ticket type is required")
        TicketType ticketType
) {}
