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

    @NotNull(message = "La película es obligatoria")
    private Long movieId;

    @NotNull(message = "La sala es obligatoria")
    private Long theaterId;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha de la proyección debe ser futura")
    private LocalDateTime startTime;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal basePrice;
}
