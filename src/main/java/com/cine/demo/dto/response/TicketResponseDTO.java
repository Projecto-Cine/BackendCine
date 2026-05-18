package com.cine.demo.dto.response;

import com.cine.demo.model.enums.TicketType;
import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record TicketResponseDTO(
        Long id,
        Long purchaseId,
        Long seatId,
        String row,
        int number,
        String seatType,
        TicketType ticketType,
        BigDecimal unitPrice
) {}
