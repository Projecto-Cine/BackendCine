package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MerchandiseSaleResponseDTO {
    private Long id;
    private Long userId;
    private Long merchandiseId;
    private String merchandiseName;
    private int quantity;
    private BigDecimal total;
    private LocalDateTime saleDate;
}
