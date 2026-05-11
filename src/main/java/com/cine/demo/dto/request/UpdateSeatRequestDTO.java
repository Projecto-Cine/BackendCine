package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatRequestDTO {

    @Size(max = 5, message = "Row must not exceed 5 characters")
    private String fila;

    @Min(value = 1, message = "Seat number must be at least 1")
    private Integer numero;

    private String tipo;
}
