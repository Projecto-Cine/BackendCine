package com.cine.demo.dto.request;

import com.cine.demo.model.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {

    @NotNull(message = "Seat is required")
    private Long screeningSeatId;

    @NotNull(message = "Ticket type is required")
    private TicketType ticketType;
}
