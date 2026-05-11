package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketOfficeRequestDTO {

    @NotNull(message = "La proyección es obligatoria")
    private Long screeningId;

    @NotEmpty(message = "Debe seleccionar al menos un asiento")
    private List<String> seats;

    @NotNull(message = "El tipo de ticket es obligatorio")
    private String ticketType;

    @NotNull(message = "El precio unitario es obligatorio")
    private BigDecimal unitPrice;

    private BigDecimal surcharge;

    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    @NotNull(message = "El método de pago es obligatorio")
    private String paymentMethod;

    @NotNull(message = "El cajero es obligatorio")
    private Long cashierId;

    private Long userId;
}
