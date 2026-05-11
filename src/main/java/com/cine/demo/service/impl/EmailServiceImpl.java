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
        String subject = "Confirmación de compra #" + purchase.getId() + " - Lumen Cinema";
        String body = buildConfirmationBody(purchase);
        sendEmail(purchase.getUser().getEmail(), subject, body);
    }

    private String buildConfirmationBody(Purchase purchase) {
        var screening = purchase.getScreening();
        StringBuilder sb = new StringBuilder();
        sb.append("¡Gracias por tu compra en Lumen Cinema!\n\n");
        sb.append("=== CONFIRMACIÓN DE COMPRA #").append(purchase.getId()).append(" ===\n\n");
        sb.append("Película:     ").append(screening.getMovie().getTitle()).append("\n");
        sb.append("Sala:         ").append(screening.getTheater().getName()).append("\n");
        sb.append("Fecha y hora: ").append(screening.getStartTime()).append("\n\n");
        sb.append("ENTRADAS:\n");
        for (Ticket ticket : purchase.getTickets()) {
            sb.append("  - Sala ").append(screening.getTheater().getName())
              .append(" | Fila ").append(ticket.getSeat().getRow())
              .append(", Butaca ").append(ticket.getSeat().getNumber())
              .append(" (").append(ticket.getTicketType()).append(")")
              .append(" — ").append(ticket.getUnitPrice()).append("€\n");
        }
        sb.append("\n");
        if (purchase.isDiscountApplied()) {
            sb.append("Descuento aplicado: -").append(purchase.getDiscountAmount()).append("€\n");
        }
        sb.append("TOTAL: ").append(purchase.getTotalAmount()).append("€\n\n");
        sb.append("¡Disfruta de la película!\n");
        sb.append("El equipo de Lumen Cinema");
        return sb.toString();
    }
}
