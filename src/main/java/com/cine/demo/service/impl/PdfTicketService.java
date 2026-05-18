package com.cine.demo.service.impl;

import com.cine.demo.model.Purchase;
import com.cine.demo.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfTicketService {

    private static final Color DARK_BG      = new Color(13, 13, 26);
    private static final Color GOLD         = new Color(240, 192, 64);
    private static final Color LIGHT_BG     = new Color(249, 249, 249);
    private static final Color BORDER_LIGHT = new Color(230, 230, 230);
    private static final Color TEXT_MUTED   = new Color(150, 150, 150);

    public byte[] generateTicketPdf(Purchase purchase) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 45, 45, 45, 45);
            PdfWriter.getInstance(doc, out);
            doc.open();

            var screening = purchase.getScreening();
            String dateStr  = screening.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String timeStr  = screening.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String movie    = screening.getMovie().getTitle();
            String theater  = screening.getTheater().getName();

            // Header
            Font cinemaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, GOLD);
            Paragraph cinemaTitle = new Paragraph("LUMEN CINEMA", cinemaFont);
            cinemaTitle.setAlignment(Element.ALIGN_CENTER);
            cinemaTitle.setSpacingAfter(4f);
            doc.add(cinemaTitle);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED);
            Paragraph purchaseRef = new Paragraph(
                "Booking Confirmation / Confirmación de Compra  #" + purchase.getId(), subFont);
            purchaseRef.setAlignment(Element.ALIGN_CENTER);
            purchaseRef.setSpacingAfter(24f);
            doc.add(purchaseRef);

            // Per-ticket tables
            Font labelFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,  TEXT_MUTED);
            Font valueFont    = FontFactory.getFont(FontFactory.HELVETICA,      11, Color.DARK_GRAY);
            Font boldValue    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);
            Font typeFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, GOLD);
            Font priceFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.BLACK);
            Font idFont       = FontFactory.getFont(FontFactory.HELVETICA,      7,  TEXT_MUTED);
            Font headerFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, DARK_BG);

            int ticketNum = 1;
            for (Ticket ticket : purchase.getTickets()) {
                String qrContent = String.format(
                    "LUMEN-CINEMA|PURCHASE:%d|TICKET:%d|SEAT:%s%d|MOVIE:%s|DATE:%s %s",
                    purchase.getId(), ticket.getId(),
                    ticket.getSeat().getRow(), ticket.getSeat().getNumber(),
                    movie, dateStr, timeStr);

                byte[] qrBytes = generateQrCode(qrContent, 130);
                Image qrImage  = Image.getInstance(qrBytes);
                qrImage.scaleToFit(115, 115);

                PdfPTable table = new PdfPTable(new float[]{3.2f, 1.5f});
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(8f);

                // Left cell — ticket info
                PdfPCell infoCell = new PdfPCell();
                infoCell.setPadding(14f);
                infoCell.setBackgroundColor(LIGHT_BG);
                infoCell.setBorderColor(GOLD);
                infoCell.setBorderWidth(1.2f);

                Paragraph ticketHeader = new Paragraph(
                    "TICKET #" + ticketNum + "  /  ENTRADA #" + ticketNum, headerFont);
                ticketHeader.setSpacingAfter(10f);
                infoCell.addElement(ticketHeader);

                addRow(infoCell, "Movie / Película",  movie,  labelFont, boldValue);
                addRow(infoCell, "Theater / Sala",          theater, labelFont, valueFont);
                addRow(infoCell, "Date / Fecha",            dateStr, labelFont, valueFont);
                addRow(infoCell, "Time / Hora",             timeStr, labelFont, valueFont);
                addRow(infoCell, "Seat / Butaca",
                    "Row " + ticket.getSeat().getRow() + ", Seat " + ticket.getSeat().getNumber()
                    + "  /  Fila " + ticket.getSeat().getRow() + ", Butaca " + ticket.getSeat().getNumber(),
                    labelFont, valueFont);

                String typeName = ticket.getTicketType().name();
                addRow(infoCell, "Type / Tipo", typeName + " / " + translateType(typeName), labelFont, typeFont);
                addRow(infoCell, "Price / Precio", "€" + ticket.getUnitPrice().toPlainString(), labelFont, priceFont);

                table.addCell(infoCell);

                // Right cell — QR code
                PdfPCell qrCell = new PdfPCell();
                qrCell.setPadding(10f);
                qrCell.setBackgroundColor(Color.WHITE);
                qrCell.setBorderColor(GOLD);
                qrCell.setBorderWidth(1.2f);
                qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                qrImage.setAlignment(Image.ALIGN_CENTER);
                qrCell.addElement(qrImage);

                Paragraph ticketId = new Paragraph(
                    "LUMEN-" + purchase.getId() + "-" + ticket.getId(), idFont);
                ticketId.setAlignment(Element.ALIGN_CENTER);
                ticketId.setSpacingBefore(8f);
                qrCell.addElement(ticketId);

                table.addCell(qrCell);
                doc.add(table);
                ticketNum++;
            }

            // Totals table
            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(100);
            totals.setSpacingBefore(6f);

            if (purchase.isDiscountApplied()) {
                Font discFont    = FontFactory.getFont(FontFactory.HELVETICA,      10, TEXT_MUTED);
                Font discValFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(200, 60, 60));
                addTotalRow(totals,
                    "Loyalty Discount / Descuento fidelidad",
                    "−€" + purchase.getDiscountAmount().toPlainString(),
                    discFont, discValFont, false);
            }

            Font totalLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            Font totalValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, GOLD);
            addTotalRow(totals, "TOTAL",
                "€" + purchase.getTotalAmount().toPlainString(),
                totalLabel, totalValue, true);
            doc.add(totals);

            // Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
            Paragraph footer = new Paragraph(
                "\nEnjoy the movie!  /  ¡Disfruta la película!\n"
                + "Lumen Cinema · equipo2lumencinema@gmail.com", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(18f);
            doc.add(footer);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating ticket PDF", e);
        }
    }

    private void addRow(PdfPCell cell, String label, String value, Font labelFont, Font valueFont) {
        Paragraph lbl = new Paragraph(label, labelFont);
        lbl.setSpacingBefore(5f);
        cell.addElement(lbl);
        Paragraph val = new Paragraph(value, valueFont);
        val.setSpacingAfter(3f);
        cell.addElement(val);
    }

    private void addTotalRow(PdfPTable table, String label, String value,
                             Font labelFont, Font valueFont, boolean highlight) {
        Color borderColor = highlight ? GOLD : BORDER_LIGHT;
        float borderWidth = highlight ? 2f : 0.5f;

        PdfPCell lbl = new PdfPCell(new Phrase(label, labelFont));
        lbl.setPadding(10f);
        lbl.setBorder(Rectangle.TOP);
        lbl.setBorderColor(borderColor);
        lbl.setBorderWidthTop(borderWidth);
        table.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, valueFont));
        val.setPadding(10f);
        val.setHorizontalAlignment(Element.ALIGN_RIGHT);
        val.setBorder(Rectangle.TOP);
        val.setBorderColor(borderColor);
        val.setBorderWidthTop(borderWidth);
        table.addCell(val);
    }

    private byte[] generateQrCode(String content, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", png);
        return png.toByteArray();
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
