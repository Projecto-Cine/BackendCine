package com.cine.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record UpdateScreeningRequestDTO(
        @Future(message = "Screening date must be in the future")
        LocalDateTime startTime,
        @DecimalMin(value = "0.0", message = "Price cannot be negative")
        BigDecimal basePrice
) {}
