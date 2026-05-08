package com.cine.demo.dto.response;

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
    private Long userId;
    private String userNombre;
    private Long screeningId;
    private String movieTitulo;
    private String theaterNombre;
    private LocalDateTime fechaHora;
    private List<TicketResponseDTO> tickets;
    private BigDecimal totalAmount;
    private boolean discountApplied;
    private BigDecimal discountAmount;
    private PurchaseStatus status;
    private LocalDateTime createdAt;
}
