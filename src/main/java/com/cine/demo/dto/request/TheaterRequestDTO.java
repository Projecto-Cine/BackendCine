package com.cine.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterRequestDTO {

    @NotBlank(message = "El nombre de la sala es obligatorio")
    private String name;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private int capacity;

    private String status;
}