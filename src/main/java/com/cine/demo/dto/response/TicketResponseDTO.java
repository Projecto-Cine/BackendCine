package com.cine.demo.dto.response;

import com.cine.demo.model.enums.TicketType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TicketResponseDTO {
    private Long id;
    private Long purchaseId;
    private Long seatId;
    private String row;
    private int number;
    private String seatType;
    private TicketType ticketType;
    private BigDecimal unitPrice;
}