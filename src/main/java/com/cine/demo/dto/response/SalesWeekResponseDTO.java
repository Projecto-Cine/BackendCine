package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SalesWeekResponseDTO {
    private LocalDate date;
    private long totalPurchases;
    private BigDecimal revenue;
}
