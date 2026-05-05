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

        List<String> seats = purchase.getTickets().stream()
                .map(t -> t.getSeat().getRow() + t.getSeat().getNumber())
                .toList();

        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .clientName(purchase.getUser().getName())
                .clientEmail(purchase.getUser().getEmail())
                .screeningId(purchase.getScreening().getId())
                .movieTitle(purchase.getScreening().getMovie().getTitle())
                .theaterName(purchase.getScreening().getTheater().getName())
                .dateTime(purchase.getScreening().getDateTime())
                .screening(PurchaseResponseDTO.ScreeningInfo.builder()
                        .id(purchase.getScreening().getId())
                        .dateTime(purchase.getScreening().getDateTime())
                        .movie(PurchaseResponseDTO.MovieInfo.builder()
                                .title(purchase.getScreening().getMovie().getTitle())
                                .build())
                        .theater(PurchaseResponseDTO.TheaterInfo.builder()
                                .name(purchase.getScreening().getTheater().getName())
                                .build())
                        .build())
                .seats(seats)
                .tickets(ticketDtos)
                .totalAmount(purchase.getTotalAmount())
                .discountApplied(purchase.isDiscountApplied())
                .discountAmount(purchase.getDiscountAmount())
                .status(purchase.getStatus())
                .paymentMethod(purchase.getPaymentMethod())
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
        String date = ticket.getScreening().getDateTime().toLocalDate().toString();
        String time = ticket.getScreening().getDateTime().toLocalTime().toString().substring(0, 5);
        String seat = ticket.getSeat().getRow() + ticket.getSeat().getNumber();
        String movie = ticket.getScreening().getMovie().getTitle();
        String theater = ticket.getScreening().getTheater().getName();

        String qrCode = String.format(
                "LUMEN:TKT-%d|%s|%s|%s|%s|%s|%s",
                ticket.getId(), movie, theater, date, time, seat,
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