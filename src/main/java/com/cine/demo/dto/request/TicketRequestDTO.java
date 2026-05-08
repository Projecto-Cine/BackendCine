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

    @NotNull(message = "El asiento es obligatorio")
    private Long seatId;

    @NotNull(message = "El tipo de entrada es obligatorio")
    private TicketType ticketType;
}
