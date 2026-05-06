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
public class PaymentResponseDTO {
    private Long purchaseId;
    private PurchaseStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private boolean discountApplied;
    private String movieTitle;
    private String theaterName;
    private LocalDateTime screeningDateTime;
    private List<TicketResponseDTO> tickets;
    // Only populated for QR payment method — the frontend renders it with qrcode.react
    private String paymentQrCode;
}