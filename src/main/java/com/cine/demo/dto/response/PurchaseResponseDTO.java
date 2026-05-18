package com.cine.demo.dto.response;

import com.cine.demo.model.enums.PaymentMethod;
import com.cine.demo.model.enums.PurchaseStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PurchaseResponseDTO(
        Long id,
        Long userId,
        String userName,
        Long screeningId,
        String movieTitle,
        String theaterName,
        LocalDateTime startTime,
        List<TicketResponseDTO> tickets,
        BigDecimal totalAmount,
        boolean discountApplied,
        BigDecimal discountAmount,
        PurchaseStatus status,
        PaymentMethod paymentMethod,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {}
