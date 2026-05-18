package com.cine.demo.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record SalesWeekResponseDTO(
        LocalDate date,
        long totalPurchases,
        BigDecimal revenue
) {}
