package com.cine.demo.dto.request;

import com.cine.demo.model.enums.TicketType;
import jakarta.validation.constraints.NotNull;

public record TicketRequestDTO(
        @NotNull(message = "Seat is required")
        Long seatId,

        @NotNull(message = "Ticket type is required")
        TicketType ticketType
) {}
