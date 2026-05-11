package com.cine.demo.mapper;

import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.TicketResponseDTO;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.PurchaseStatus;
import com.cine.demo.model.enums.SeatType;
import com.cine.demo.model.enums.TicketType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseMapperTest {

    private final PurchaseMapper mapper = new PurchaseMapper();

    @Test
    void toTicketResponseDto_extractsSeatAndPurchaseIds() {
        Theater theater = Theater.builder().id(1L).nombre("Sala 1").build();
        Seat seat = Seat.builder()
                .id(11L).theater(theater).fila("B").numero(4).tipo(SeatType.VIP).build();
        Purchase purchase = Purchase.builder().id(99L).build();
        Ticket ticket = Ticket.builder()
                .id(7L).purchase(purchase).seat(seat)
                .ticketType(TicketType.ADULT).unitPrice(BigDecimal.valueOf(12.5)).build();

        TicketResponseDTO dto = mapper.toTicketResponseDto(ticket);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getPurchaseId()).isEqualTo(99L);
        assertThat(dto.getSeatId()).isEqualTo(11L);
        assertThat(dto.getFila()).isEqualTo("B");
        assertThat(dto.getNumero()).isEqualTo(4);
        assertThat(dto.getSeatType()).isEqualTo("VIP");
        assertThat(dto.getTicketType()).isEqualTo(TicketType.ADULT);
        assertThat(dto.getUnitPrice()).isEqualByComparingTo("12.5");
    }

    @Test
    void toResponseDto_mapsAllPurchaseFieldsAndNestedTickets() {
        User user = User.builder().id(1L).nombre("Ana").email("ana@cine.com").build();
        Movie movie = Movie.builder().id(2L).titulo("Matrix").build();
        Theater theater = Theater.builder().id(3L).nombre("Sala IMAX").build();
        Screening screening = Screening.builder()
                .id(4L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.of(2030, 1, 1, 22, 0)).build();
        Seat seat = Seat.builder()
                .id(5L).theater(theater).fila("A").numero(1).tipo(SeatType.STANDARD).build();

        Purchase purchase = Purchase.builder()
                .id(100L).user(user).screening(screening)
                .totalAmount(BigDecimal.valueOf(20)).discountAmount(BigDecimal.ZERO)
                .discountApplied(false).status(PurchaseStatus.PENDING)
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();
        Ticket ticket = Ticket.builder()
                .id(7L).purchase(purchase).seat(seat).screening(screening)
                .ticketType(TicketType.ADULT).unitPrice(BigDecimal.TEN).build();
        purchase.setTickets(List.of(ticket));

        PurchaseResponseDTO dto = mapper.toResponseDto(purchase);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getUserNombre()).isEqualTo("Ana");
        assertThat(dto.getScreeningId()).isEqualTo(4L);
        assertThat(dto.getMovieTitulo()).isEqualTo("Matrix");
        assertThat(dto.getTheaterNombre()).isEqualTo("Sala IMAX");
        assertThat(dto.getStatus()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(dto.isDiscountApplied()).isFalse();
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("20");
        assertThat(dto.getTickets()).hasSize(1);
        assertThat(dto.getTickets().get(0).getId()).isEqualTo(7L);
    }

    @Test
    void toResponseDto_returnsEmptyTicketList_whenNoTickets() {
        User user = User.builder().id(1L).nombre("X").build();
        Movie movie = Movie.builder().id(1L).titulo("X").build();
        Theater theater = Theater.builder().id(1L).nombre("X").build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater).fechaHora(LocalDateTime.now()).build();
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .totalAmount(BigDecimal.ZERO).discountAmount(BigDecimal.ZERO)
                .status(PurchaseStatus.PENDING).tickets(List.of()).build();

        PurchaseResponseDTO dto = mapper.toResponseDto(purchase);

        assertThat(dto.getTickets()).isEmpty();
    }
}
