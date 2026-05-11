package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PayPurchaseResponseDTO {
    private Long purchaseId;
    private String status;
    private List<TicketQrDTO> tickets;
    private String paymentQrCode;
}
