package com.cine.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ScreeningRequestDTO(
        @NotNull(message = "Movie is required")
        Long movieId,

        @NotNull(message = "Theater is required")
        Long theaterId,

        @NotNull(message = "Date and time are required")
        @Future(message = "Screening date must be in the future")
        LocalDateTime startDatetime,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
        BigDecimal basePrice
) {}
