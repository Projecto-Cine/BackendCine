package com.cine.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRequestDTO {

    @NotNull(message = "Movie is required")
    private Long movieId;

    @NotNull(message = "Theater is required")
    private Long theaterId;

    @NotNull(message = "Date and time are required")
    @Future(message = "Screening date must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal basePrice;
}
