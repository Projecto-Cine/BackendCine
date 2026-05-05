package com.cine.demo.service.impl;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.Ticket;
import com.cine.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendPurchaseConfirmation(Purchase purchase) {
        String to = purchase.getUser().getEmail();
        if (to == null || to.isBlank()) return;
        sendEmail(to, "Confirmación de compra - Lumen Cinema #" + purchase.getId(),
                buildPurchaseHtml(purchase));
    }

    private String buildPurchaseHtml(Purchase purchase) {
        var screening = purchase.getScreening();
        var movie = screening.getMovie();
        var theater = screening.getTheater();
        var dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder ticketsHtml = new StringBuilder();
        for (Ticket ticket : purchase.getTickets()) {
            String qrData = String.format(
                    "LUMEN:TKT-%d|%s|%s|%s|%s|%s|%s",
                    ticket.getId(),
                    movie.getTitle(),
                    theater.getName(),
                    screening.getDateTime().toLocalDate(),
                    screening.getDateTime().toLocalTime().toString().substring(0, 5),
                    ticket.getSeat().getRow() + ticket.getSeat().getNumber(),
                    ticket.getTicketType().name()
            );
            String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="
                    + URLEncoder.encode(qrData, StandardCharsets.UTF_8);

            ticketsHtml.append("""
                    <div style="border:1px solid #333;border-radius:8px;padding:16px;margin:8px;display:inline-block;background:#16213e;text-align:center;vertical-align:top;">
                      <p style="margin:0 0 6px;font-size:14px;color:#e0e0e0;font-weight:bold;">Asiento %s%d</p>
                      <p style="margin:0 0 4px;font-size:12px;color:#aaa;">%s &middot; %s &euro;</p>
                      <img src="%s" alt="QR Ticket" style="width:130px;height:130px;display:block;margin:8px auto;" />
                      <p style="margin:4px 0 0;font-size:11px;color:#555;">Ticket #%d</p>
                    </div>
                    """.formatted(
                    ticket.getSeat().getRow(),
                    ticket.getSeat().getNumber(),
                    ticket.getTicketType().name(),
                    ticket.getUnitPrice().toPlainString(),
                    qrUrl,
                    ticket.getId()
            ));
        }

        String genre = movie.getGenre() != null ? " &middot; " + movie.getGenre() : "";

        return """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
                <body style="margin:0;padding:0;background:#0d0d1a;font-family:Arial,Helvetica,sans-serif;color:#e0e0e0;">
                  <div style="max-width:600px;margin:0 auto;padding:32px 16px;">
                    <div style="text-align:center;margin-bottom:28px;">
                      <h1 style="color:#e50914;margin:0;font-size:32px;letter-spacing:3px;font-weight:900;">LUMEN CINEMA</h1>
                      <p style="color:#777;margin:6px 0 0;font-size:13px;letter-spacing:1px;">CONFIRMACI&Oacute;N DE COMPRA</p>
                    </div>
                    <div style="background:#16213e;border-radius:12px;padding:24px;margin-bottom:20px;border-left:4px solid #e50914;">
                      <h2 style="margin:0 0 4px;font-size:22px;color:#fff;">%s</h2>
                      <p style="margin:0 0 20px;color:#888;font-size:13px;">%s%s</p>
                      <table style="width:100%%;border-collapse:collapse;">
                        <tr>
                          <td style="padding:8px 12px 8px 0;">
                            <span style="font-size:11px;color:#555;text-transform:uppercase;letter-spacing:1px;">Sala</span><br>
                            <span style="font-size:15px;font-weight:bold;">%s</span>
                          </td>
                          <td style="padding:8px 12px;">
                            <span style="font-size:11px;color:#555;text-transform:uppercase;letter-spacing:1px;">Fecha</span><br>
                            <span style="font-size:15px;font-weight:bold;">%s</span>
                          </td>
                          <td style="padding:8px 12px;">
                            <span style="font-size:11px;color:#555;text-transform:uppercase;letter-spacing:1px;">Hora</span><br>
                            <span style="font-size:15px;font-weight:bold;">%s</span>
                          </td>
                          <td style="padding:8px 0;text-align:right;">
                            <span style="font-size:11px;color:#555;text-transform:uppercase;letter-spacing:1px;">Total</span><br>
                            <span style="font-size:20px;font-weight:bold;color:#e50914;">%s &euro;</span>
                          </td>
                        </tr>
                      </table>
                    </div>
                    <h3 style="margin:0 0 12px;font-size:12px;color:#555;text-transform:uppercase;letter-spacing:2px;">Tus entradas</h3>
                    <div style="text-align:center;">
                      %s
                    </div>
                    <div style="margin-top:28px;padding-top:20px;border-top:1px solid #1a1a2e;text-align:center;">
                      <p style="margin:0;font-size:12px;color:#444;">Referencia: <strong style="color:#666;">#%d</strong></p>
                      <p style="margin:6px 0 0;font-size:11px;color:#333;">Presenta el c&oacute;digo QR al entrar a la sala &mdash; Lumen Cinema.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(
                movie.getTitle(),
                theater.getName(),
                genre,
                theater.getName(),
                screening.getDateTime().format(dateFmt),
                screening.getDateTime().format(timeFmt),
                purchase.getTotalAmount().toPlainString(),
                ticketsHtml.toString(),
                purchase.getId()
        );
    }
}