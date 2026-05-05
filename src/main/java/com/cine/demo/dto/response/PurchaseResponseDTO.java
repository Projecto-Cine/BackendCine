package com.cine.demo.dto.response;

import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseResponseDTO {

    private Long id;

    // Client info
    private Long userId;
    private String clientName;
    private String clientEmail;

    // Screening — flat fields (kept for backward compat)
    private Long screeningId;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime dateTime;

    // Screening — embedded object (what the frontend Reservations CRUD expects)
    private ScreeningInfo screening;

    // Seats as label array ["A5", "A6"] + full ticket list
    private List<String> seats;
    private List<TicketResponseDTO> tickets;

    private BigDecimal totalAmount;
    private boolean discountApplied;
    private BigDecimal discountAmount;
    private PurchaseStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class ScreeningInfo {
        private Long id;
        private LocalDateTime dateTime;
        private MovieInfo movie;
        private TheaterInfo theater;
    }

    @Data
    @Builder
    public static class MovieInfo {
        private String title;
    }

    @Data
    @Builder
    public static class TheaterInfo {
        private String name;
    }
}