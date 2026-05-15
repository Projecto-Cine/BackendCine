package com.cine.demo.service.impl;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.Ticket;
import com.cine.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendPurchaseConfirmation(Purchase purchase) {
        String subject = "Purchase confirmation #" + purchase.getId() + " - Lumen Cinema";
        String body = buildConfirmationBody(purchase);
        String recipient = (purchase.getGuestEmail() != null && !purchase.getGuestEmail().isBlank())
                ? purchase.getGuestEmail()
                : purchase.getUser().getEmail();
        sendEmail(recipient, subject, body);
    }

    private String buildConfirmationBody(Purchase purchase) {
        var screening = purchase.getScreening();
        StringBuilder sb = new StringBuilder();
        sb.append("Thank you for your purchase at Lumen Cinema!\n\n");
        sb.append("=== PURCHASE CONFIRMATION #").append(purchase.getId()).append(" ===\n\n");
        sb.append("Movie:        ").append(screening.getMovie().getTitle()).append("\n");
        sb.append("Theater:      ").append(screening.getTheater().getName()).append("\n");
        sb.append("Date & time:  ").append(screening.getStartTime()).append("\n\n");
        sb.append("TICKETS:\n");
        for (Ticket ticket : purchase.getTickets()) {
            sb.append("  - Theater ").append(screening.getTheater().getName())
              .append(" | Row ").append(ticket.getSeat().getRow())
              .append(", Seat ").append(ticket.getSeat().getNumber())
              .append(" (").append(ticket.getTicketType()).append(")")
              .append(" — ").append(ticket.getUnitPrice()).append("\n");
        }
        sb.append("\n");
        if (purchase.isDiscountApplied()) {
            sb.append("Discount applied: -").append(purchase.getDiscountAmount()).append("\n");
        }
        sb.append("TOTAL: ").append(purchase.getTotalAmount()).append("\n\n");
        sb.append("Enjoy the movie!\n");
        sb.append("The Lumen Cinema team");
        return sb.toString();
    }
}
