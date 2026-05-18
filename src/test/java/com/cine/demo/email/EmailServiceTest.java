package com.cine.demo.email;

import com.cine.demo.model.*;
import com.cine.demo.model.enums.*;
import com.cine.demo.service.impl.EmailServiceImpl;
import com.cine.demo.service.impl.PdfTicketService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private PdfTicketService pdfTicketService;

    @InjectMocks private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "from", "no-reply@lumencinema.com");
    }

    private MimeMessage realMimeMessage() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    private Purchase buildPurchase(boolean withDiscount) {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").durationMin(148)
                .genre("Sci-Fi").ageRating(AgeRating.TWELVE).build();
        Theater theater = Theater.builder().id(1L).name("Sala 1").capacity(100).build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2025, 8, 20, 20, 30))
                .basePrice(BigDecimal.TEN).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("A").number(5).type(SeatType.STANDARD).build();
        User user = User.builder()
                .id(1L).name("Ana").email("ana@test.com").password("pass")
                .birthDate(LocalDate.of(1990, 1, 1)).role(Role.CLIENT).build();
        Purchase purchase = Purchase.builder()
                .id(100L).user(user).screening(screening)
                .status(PurchaseStatus.PAID).totalAmount(new BigDecimal("10.00"))
                .discountApplied(withDiscount)
                .discountAmount(withDiscount ? new BigDecimal("1.00") : BigDecimal.ZERO)
                .tickets(List.of()).build();
        return purchase;
    }

    private Purchase buildPurchaseWithTicket() {
        Movie movie = Movie.builder()
                .id(1L).title("Matrix").durationMin(136)
                .genre("Action").ageRating(AgeRating.SIXTEEN).build();
        Theater theater = Theater.builder().id(1L).name("Sala 2").capacity(80).build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2025, 9, 10, 18, 0))
                .basePrice(BigDecimal.TEN).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("B").number(3).type(SeatType.STANDARD).build();
        User user = User.builder()
                .id(1L).name("Carlos").email("carlos@test.com").password("pass")
                .birthDate(LocalDate.of(1985, 5, 20)).role(Role.CLIENT).build();
        Ticket ticket = Ticket.builder()
                .id(1L).seat(seat).ticketType(TicketType.ADULT).unitPrice(new BigDecimal("10.00")).build();
        Purchase purchase = Purchase.builder()
                .id(101L).user(user).screening(screening)
                .status(PurchaseStatus.PAID).totalAmount(new BigDecimal("10.00"))
                .discountApplied(false).discountAmount(BigDecimal.ZERO)
                .tickets(List.of(ticket)).build();
        ticket.setScreening(screening);
        ticket.setPurchase(purchase);
        return purchase;
    }

    @Test
    void sendEmail_sendsSimpleMailMessage_toCorrectRecipient() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail("target@test.com", "Test Subject", "Test body");

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getTo()).containsExactly("target@test.com");
    }

    @Test
    void sendEmail_setsSubjectAndText() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail("a@b.com", "My Subject", "My body text");

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getSubject()).isEqualTo("My Subject");
        assertThat(captor.getValue().getText()).isEqualTo("My body text");
    }

    @Test
    void sendEmail_setsFromAddress() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail("a@b.com", "Subject", "Body");

        verify(mailSender).send(captor.capture());
        assertThat(captor.getValue().getFrom()).isEqualTo("no-reply@lumencinema.com");
    }

    @Test
    void sendPurchaseConfirmation_sendsMimeMessage_toUserEmail_whenNoGuestEmail() {
        Purchase purchase = buildPurchase(false);
        when(pdfTicketService.generateTicketPdf(purchase)).thenReturn(new byte[]{1, 2, 3});
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        emailService.sendPurchaseConfirmation(purchase);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPurchaseConfirmation_sendsMimeMessage_toGuestEmail_whenPresent() {
        Purchase purchase = buildPurchase(false);
        purchase.setGuestEmail("guest@test.com");
        when(pdfTicketService.generateTicketPdf(purchase)).thenReturn(new byte[]{1, 2, 3});
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        emailService.sendPurchaseConfirmation(purchase);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPurchaseConfirmation_withDiscountApplied_sendsMimeMessage() {
        Purchase purchase = buildPurchase(true);
        when(pdfTicketService.generateTicketPdf(purchase)).thenReturn(new byte[]{1, 2, 3});
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        emailService.sendPurchaseConfirmation(purchase);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPurchaseConfirmation_withTickets_sendsMimeMessage() {
        Purchase purchase = buildPurchaseWithTicket();
        when(pdfTicketService.generateTicketPdf(purchase)).thenReturn(new byte[]{1, 2, 3});
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        emailService.sendPurchaseConfirmation(purchase);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendMemberWelcome_sendsMimeMessage() {
        when(mailSender.createMimeMessage()).thenReturn(realMimeMessage());

        emailService.sendMemberWelcome("member@test.com", "Laura");

        verify(mailSender).send(any(MimeMessage.class));
    }
}
