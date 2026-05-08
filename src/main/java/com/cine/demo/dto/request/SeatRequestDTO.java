package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDTO {

    @NotNull(message = "El id de sala es obligatorio")
    private Long theaterId;

    @NotBlank(message = "La fila es obligatoria")
    private String fila;

    @Min(value = 1, message = "El número de asiento debe ser al menos 1")
    private int numero;

    @NotNull(message = "El tipo de asiento es obligatorio")
    private String tipo;
}
