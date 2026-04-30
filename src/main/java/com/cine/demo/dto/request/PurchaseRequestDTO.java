package com.cine.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @NotNull(message = "La proyección es obligatoria")
    private Long screeningId;

    @NotEmpty(message = "La compra debe incluir al menos un ticket")
    @Size(min = 1, message = "La compra debe incluir al menos un ticket")
    @Valid
    private List<TicketRequestDTO> tickets;
}
