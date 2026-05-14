package com.cine.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
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
public class UpdateScreeningRequestDTO {

    @Future(message = "Screening date must be in the future")
    private LocalDateTime startTime;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal basePrice;
}
