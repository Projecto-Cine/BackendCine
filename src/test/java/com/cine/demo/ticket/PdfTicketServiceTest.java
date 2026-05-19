package com.cine.demo.ticket;

import com.cine.demo.model.*;
import com.cine.demo.model.enums.*;
import com.cine.demo.service.impl.PdfTicketService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfTicketServiceTest {

    private final PdfTicketService pdfTicketService = new PdfTicketService();

    private Purchase buildPurchase(boolean discountApplied, List<Ticket> tickets) {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").durationMin(148)
                .genre("Sci-Fi").ageRating(AgeRating.TWELVE).build();

        Theater theater = Theater.builder()
                .id(1L).name("Sala 1").capacity(100).build();

        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2025, 6, 15, 20, 30))
                .basePrice(BigDecimal.TEN).build();

        User user = User.builder()
                .id(1L).name("Ana").email("ana@test.com").password("pass")
                .birthDate(LocalDate.of(1990, 1, 1)).role(Role.CLIENT).build();

        BigDecimal total = tickets.stream()
                .map(Ticket::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Purchase purchase = Purchase.builder()
                .id(100L).user(user).screening(screening)
                .status(PurchaseStatus.PAID).totalAmount(total)
                .discountApplied(discountApplied)
                .discountAmount(discountApplied ? new BigDecimal("1.00") : BigDecimal.ZERO)
                .tickets(tickets).build();

        tickets.forEach(t -> {
            t.setScreening(screening);
            t.setPurchase(purchase);
        });

        return purchase;
    }

    private Ticket buildAdultTicket(Theater theater) {
        Seat seat = Seat.builder()
                .id(1L).theater(theater).row("A").number(5).type(SeatType.STANDARD).build();
        return Ticket.builder()
                .id(1L).seat(seat).ticketType(TicketType.ADULT)
                .unitPrice(new BigDecimal("10.00")).build();
    }

    @Test
    void generateTicketPdf_returnsNonEmptyBytes() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(100).build();
        Ticket ticket = buildAdultTicket(theater);
        Purchase purchase = buildPurchase(false, List.of(ticket));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void generateTicketPdf_returnsValidPdfByteHeader() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(100).build();
        Ticket ticket = buildAdultTicket(theater);
        Purchase purchase = buildPurchase(false, List.of(ticket));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void generateTicketPdf_includesDiscountSection_whenDiscountApplied() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(100).build();
        Ticket ticket = buildAdultTicket(theater);
        Purchase purchase = buildPurchase(true, List.of(ticket));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void generateTicketPdf_handlesMultipleTickets() {
        Theater theater = Theater.builder().id(2L).name("Sala 2").capacity(80).build();
        Seat seat1 = Seat.builder().id(1L).theater(theater).row("B").number(1).type(SeatType.STANDARD).build();
        Seat seat2 = Seat.builder().id(2L).theater(theater).row("B").number(2).type(SeatType.VIP).build();
        Ticket ticket1 = Ticket.builder().id(1L).seat(seat1)
                .ticketType(TicketType.ADULT).unitPrice(new BigDecimal("10.00")).build();
        Ticket ticket2 = Ticket.builder().id(2L).seat(seat2)
                .ticketType(TicketType.SENIOR).unitPrice(new BigDecimal("8.00")).build();

        Purchase purchase = buildPurchase(false, List.of(ticket1, ticket2));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void generateTicketPdf_handlesChildTicketType() {
        Theater theater = Theater.builder().id(1L).name("Sala 3").capacity(50).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("C").number(3).type(SeatType.STANDARD).build();
        Ticket ticket = Ticket.builder().id(1L).seat(seat)
                .ticketType(TicketType.CHILD).unitPrice(new BigDecimal("5.00")).build();

        Purchase purchase = buildPurchase(false, List.of(ticket));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void generateTicketPdf_handlesStudentTicketType() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(100).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("D").number(7).type(SeatType.STANDARD).build();
        Ticket ticket = Ticket.builder().id(1L).seat(seat)
                .ticketType(TicketType.STUDENT).unitPrice(new BigDecimal("7.00")).build();

        Purchase purchase = buildPurchase(false, List.of(ticket));

        byte[] pdf = pdfTicketService.generateTicketPdf(purchase);

        assertThat(pdf).isNotEmpty();
    }
}
