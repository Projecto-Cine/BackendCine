package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeatRequestDTO {
    private String row;

    @Min(value = 1, message = "El número de asiento debe ser al menos 1")
    private Integer number;

    private String type;
}