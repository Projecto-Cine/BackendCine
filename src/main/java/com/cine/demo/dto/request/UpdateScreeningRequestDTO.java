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

    @Future(message = "La fecha de la proyección debe ser futura")
    private LocalDateTime fechaHora;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioBase;
}
