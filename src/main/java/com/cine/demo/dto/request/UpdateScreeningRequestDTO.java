package com.cine.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateScreeningRequestDTO(
        @Future(message = "Screening date must be in the future")
        LocalDateTime startDatetime,

        @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
        BigDecimal basePrice
) {}
