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
                .userNombre(purchase.getUser().getNombre())
                .screeningId(purchase.getScreening().getId())
                .movieTitulo(purchase.getScreening().getMovie().getTitle())
                .theaterNombre(purchase.getScreening().getTheater().getNombre())
                .fechaHora(purchase.getScreening().getFechaHora())
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
                .fila(ticket.getSeat().getFila())
                .numero(ticket.getSeat().getNumero())
                .seatType(ticket.getSeat().getTipo().name())
                .ticketType(ticket.getTicketType())
                .unitPrice(ticket.getUnitPrice())
                .build();
    }
}
