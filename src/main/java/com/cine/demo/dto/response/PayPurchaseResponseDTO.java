package com.cine.demo.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record PayPurchaseResponseDTO(
        Long purchaseId,
        String status,
        List<TicketQrDTO> tickets,
        String paymentQrCode
) {}
