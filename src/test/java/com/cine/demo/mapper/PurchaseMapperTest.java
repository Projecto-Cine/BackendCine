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
        Theater theater = Theater.builder().id(1L).name("Sala 1").build();
        Seat seat = Seat.builder()
                .id(11L).theater(theater).row("B").number(4).type(SeatType.VIP).build();
        Purchase purchase = Purchase.builder().id(99L).build();
        Ticket ticket = Ticket.builder()
                .id(7L).purchase(purchase).seat(seat)
                .ticketType(TicketType.ADULT).unitPrice(BigDecimal.valueOf(12.5)).build();

        TicketResponseDTO dto = mapper.toTicketResponseDto(ticket);

        assertThat(dto.id()).isEqualTo(7L);
        assertThat(dto.purchaseId()).isEqualTo(99L);
        assertThat(dto.seatId()).isEqualTo(11L);
        assertThat(dto.row()).isEqualTo("B");
        assertThat(dto.number()).isEqualTo(4);
        assertThat(dto.seatType()).isEqualTo("VIP");
        assertThat(dto.ticketType()).isEqualTo(TicketType.ADULT);
        assertThat(dto.unitPrice()).isEqualByComparingTo("12.5");
    }

    @Test
    void toTicketResponseDto_mapsStandardSeatType() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").build();
        Seat seat = Seat.builder()
                .id(1L).theater(theater).row("A").number(1).type(SeatType.STANDARD).build();
        Purchase purchase = Purchase.builder().id(1L).build();
        Ticket ticket = Ticket.builder()
                .id(1L).purchase(purchase).seat(seat)
                .ticketType(TicketType.CHILD).unitPrice(BigDecimal.valueOf(6.0)).build();

        TicketResponseDTO dto = mapper.toTicketResponseDto(ticket);

        assertThat(dto.seatType()).isEqualTo("STANDARD");
        assertThat(dto.ticketType()).isEqualTo(TicketType.CHILD);
    }

    @Test
    void toResponseDto_mapsAllPurchaseFieldsAndNestedTickets() {
        User user = User.builder().id(1L).name("Ana").email("ana@cine.com").build();
        Movie movie = Movie.builder().id(2L).title("Matrix").build();
        Theater theater = Theater.builder().id(3L).name("Sala IMAX").build();
        Screening screening = Screening.builder()
                .id(4L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2030, 1, 1, 22, 0)).build();
        Seat seat = Seat.builder()
                .id(5L).theater(theater).row("A").number(1).type(SeatType.STANDARD).build();

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

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.userId()).isEqualTo(1L);
        assertThat(dto.userName()).isEqualTo("Ana");
        assertThat(dto.screeningId()).isEqualTo(4L);
        assertThat(dto.movieTitle()).isEqualTo("Matrix");
        assertThat(dto.theaterName()).isEqualTo("Sala IMAX");
        assertThat(dto.screening()).isNotNull();
        assertThat(dto.screening().id()).isEqualTo(4L);
        assertThat(dto.screening().movie().title()).isEqualTo("Matrix");
        assertThat(dto.screening().theater().name()).isEqualTo("Sala IMAX");
        assertThat(dto.screening().theater().capacity()).isZero();
        assertThat(dto.screening().startTime()).isEqualTo(LocalDateTime.of(2030, 1, 1, 22, 0));
        assertThat(dto.status()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(dto.discountApplied()).isFalse();
        assertThat(dto.totalAmount()).isEqualByComparingTo("20");
        assertThat(dto.tickets()).hasSize(1);
        assertThat(dto.tickets().get(0).id()).isEqualTo(7L);
    }

    @Test
    void toResponseDto_mapsDiscountAppliedAndAmount() {
        User user = User.builder().id(1L).name("Bob").build();
        Movie movie = Movie.builder().id(1L).title("Dune").build();
        Theater theater = Theater.builder().id(1L).name("Sala 2").build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater).startTime(LocalDateTime.now()).build();
        Purchase purchase = Purchase.builder()
                .id(10L).user(user).screening(screening)
                .totalAmount(BigDecimal.valueOf(18)).discountAmount(BigDecimal.valueOf(2))
                .discountApplied(true).status(PurchaseStatus.PAID)
                .tickets(List.of()).build();

        PurchaseResponseDTO dto = mapper.toResponseDto(purchase);

        assertThat(dto.discountApplied()).isTrue();
        assertThat(dto.discountAmount()).isEqualByComparingTo("2");
        assertThat(dto.status()).isEqualTo(PurchaseStatus.PAID);
    }

    @Test
    void toResponseDto_returnsEmptyTicketList_whenNoTickets() {
        User user = User.builder().id(1L).name("X").build();
        Movie movie = Movie.builder().id(1L).title("X").build();
        Theater theater = Theater.builder().id(1L).name("X").build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater).startTime(LocalDateTime.now()).build();
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .totalAmount(BigDecimal.ZERO).discountAmount(BigDecimal.ZERO)
                .status(PurchaseStatus.PENDING).tickets(List.of()).build();

        PurchaseResponseDTO dto = mapper.toResponseDto(purchase);

        assertThat(dto.tickets()).isEmpty();
    }

    @Test
    void toResponseDto_mapsStartTimeFromScreening() {
        LocalDateTime startTime = LocalDateTime.of(2030, 6, 15, 20, 30);
        User user = User.builder().id(1L).name("Ana").build();
        Movie movie = Movie.builder().id(1L).title("Oppenheimer").build();
        Theater theater = Theater.builder().id(1L).name("Sala 3").build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater).startTime(startTime).build();
        Purchase purchase = Purchase.builder()
                .id(1L).user(user).screening(screening)
                .totalAmount(BigDecimal.TEN).discountAmount(BigDecimal.ZERO)
                .status(PurchaseStatus.PENDING).tickets(List.of()).build();

        PurchaseResponseDTO dto = mapper.toResponseDto(purchase);

        assertThat(dto.startTime()).isEqualTo(startTime);
    }
}
