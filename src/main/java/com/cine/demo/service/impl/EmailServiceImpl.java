package com.cine.demo.service.impl;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.Ticket;
import com.cine.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final PdfTicketService pdfTicketService;

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
        String recipient = (purchase.getGuestEmail() != null && !purchase.getGuestEmail().isBlank())
                ? purchase.getGuestEmail()
                : purchase.getUser().getEmail();
        String subject = "Purchase Confirmation #" + purchase.getId() + " — Lumen Cinema";

        try {
            byte[] pdfBytes = pdfTicketService.generateTicketPdf(purchase);
            String htmlBody  = buildHtmlEmail(purchase);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(
                "tickets-lumen-" + purchase.getId() + ".pdf",
                new ByteArrayResource(pdfBytes),
                "application/pdf"
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send purchase confirmation email", e);
        }
    }

    private String buildHtmlEmail(Purchase purchase) {
        var screening   = purchase.getScreening();
        String movieTitle  = screening.getMovie().getTitle();
        String theaterName = screening.getTheater().getName();
        String dateStr = screening.getStartTime()
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH));
        String timeStr = screening.getStartTime()
            .format(DateTimeFormatter.ofPattern("HH:mm"));
        String purchaseDateStr = purchase.getCreatedAt() != null
            ? purchase.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            : "";

        StringBuilder ticketsHtml = new StringBuilder();
        for (Ticket ticket : purchase.getTickets()) {
            ticketsHtml.append(buildTicketRow(ticket));
        }

        String discountRow = purchase.isDiscountApplied()
            ? "<tr>"
              + "<td style=\"padding:10px 0 0;font-size:13px;color:#888;\">"
              + "Loyalty Discount&nbsp;/&nbsp;Descuento fidelidad</td>"
              + "<td align=\"right\" style=\"padding:10px 0 0;font-size:13px;color:#e05050;\">"
              + "&minus;&euro;" + purchase.getDiscountAmount().toPlainString() + "</td>"
              + "</tr>"
            : "";

        return loadTemplate()
            .replace("{{PURCHASE_ID}}",    String.valueOf(purchase.getId()))
            .replace("{{PURCHASE_DATE}}",  purchaseDateStr)
            .replace("{{MOVIE_TITLE}}",    escapeHtml(movieTitle))
            .replace("{{THEATER_NAME}}",   escapeHtml(theaterName))
            .replace("{{SCREENING_DATE}}", escapeHtml(dateStr))
            .replace("{{SCREENING_TIME}}", timeStr)
            .replace("{{TICKETS_ROWS}}",   ticketsHtml.toString())
            .replace("{{DISCOUNT_ROW}}",   discountRow)
            .replace("{{TOTAL_AMOUNT}}",   purchase.getTotalAmount().toPlainString());
    }

    private String buildTicketRow(Ticket ticket) {
        String seatRow = ticket.getSeat().getRow();
        int    seatNum = ticket.getSeat().getNumber();
        String typeName = ticket.getTicketType().name();
        String typeEs   = translateType(typeName);
        String price    = ticket.getUnitPrice().toPlainString();

        return String.format(
            "<table width=\"100%%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\""
            + " style=\"margin-bottom:8px;\">"
            + "<tr>"
            + "<td style=\"padding:12px 14px;background:#f8f8f8;"
            + "border:1px solid #eee;border-radius:4px 0 0 4px;\">"
            + "<p style=\"margin:0;font-size:14px;font-weight:bold;color:#111;\">"
            + "Row %s, Seat %d</p>"
            + "<p style=\"margin:3px 0 0;font-size:11px;color:#bbb;font-style:italic;\">"
            + "Fila %s, Butaca %d</p>"
            + "</td>"
            + "<td align=\"center\" style=\"padding:12px;background:#f8f8f8;"
            + "border-top:1px solid #eee;border-bottom:1px solid #eee;width:120px;\">"
            + "<span style=\"display:inline-block;background:#f0c040;color:#0d0d1a;"
            + "padding:3px 10px;border-radius:3px;font-size:11px;font-weight:bold;\">%s</span>"
            + "<p style=\"margin:4px 0 0;font-size:10px;color:#bbb;font-style:italic;\">%s</p>"
            + "</td>"
            + "<td align=\"right\" style=\"padding:12px 14px;background:#f8f8f8;"
            + "border:1px solid #eee;border-radius:0 4px 4px 0;"
            + "font-size:16px;font-weight:bold;color:#111;white-space:nowrap;width:80px;\">"
            + "&euro;%s"
            + "</td>"
            + "</tr></table>",
            seatRow, seatNum, seatRow, seatNum, typeName, typeEs, price
        );
    }

    private String loadTemplate() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("email/purchase-confirmation.html")) {
            if (is == null) throw new IllegalStateException("Email template not found");
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }

    private String translateType(String type) {
        return switch (type) {
            case "ADULT"   -> "ADULTO";
            case "CHILD"   -> "NIÑO";
            case "STUDENT" -> "ESTUDIANTE";
            case "SENIOR"  -> "JUBILADO";
            default        -> type;
        };
    }
}
