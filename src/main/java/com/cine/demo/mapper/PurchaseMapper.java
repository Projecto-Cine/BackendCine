package com.cine.demo.mapper;

import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.model.Purchase;
import com.cine.demo.model.Ticket;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PurchaseMapper {

    public PurchaseResponseDTO toResponseDto(Purchase purchase) {
        List<TicketResponseDTO> ticketDtos = purchase.getTickets().stream()
                .map(this::toTicketResponseDto)
                .toList();

        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .clientName(purchase.getUser().getName())
                .screeningId(purchase.getScreening().getId())
                .movieTitle(purchase.getScreening().getMovie().getTitle())
                .theaterName(purchase.getScreening().getTheater().getName())
                .dateTime(purchase.getScreening().getDateTime())
                .tickets(ticketDtos)
                .totalAmount(purchase.getTotalAmount())
                .discountApplied(purchase.isDiscountApplied())
                .discountAmount(purchase.getDiscountAmount())
                .status(purchase.getStatus())
                .createdAt(purchase.getCreatedAt())
                .build();
    }

    public TicketResponseDTO toTicketResponseDto(Ticket ticket) {
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .purchaseId(ticket.getPurchase().getId())
                .seatId(ticket.getSeat().getId())
                .row(ticket.getSeat().getRow())
                .number(ticket.getSeat().getNumber())
                .seatType(ticket.getSeat().getType().name())
                .ticketType(ticket.getTicketType())
                .unitPrice(ticket.getUnitPrice())
                .build();
    }

    public TicketResponseDTO toTicketResponseDtoWithQr(Ticket ticket) {
        String qrCode = String.format(
                "LUMEN:TKT-%d|%s|%s|%s|%s|%s|%s",
                ticket.getId(),
                ticket.getScreening().getMovie().getTitle(),
                ticket.getScreening().getTheater().getName(),
                ticket.getScreening().getDateTime().toLocalDate(),
                ticket.getScreening().getDateTime().toLocalTime().toString().substring(0, 5),
                ticket.getSeat().getRow() + ticket.getSeat().getNumber(),
                ticket.getTicketType().name()
        );
        return TicketResponseDTO.builder()
                .id(ticket.getId())
                .purchaseId(ticket.getPurchase().getId())
                .seatId(ticket.getSeat().getId())
                .row(ticket.getSeat().getRow())
                .number(ticket.getSeat().getNumber())
                .seatType(ticket.getSeat().getType().name())
                .ticketType(ticket.getTicketType())
                .unitPrice(ticket.getUnitPrice())
                .qrCode(qrCode)
                .build();
    }
}
