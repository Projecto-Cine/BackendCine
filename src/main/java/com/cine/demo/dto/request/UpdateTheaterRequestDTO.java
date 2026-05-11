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
public class UpdateTheaterRequestDTO {

    @Size(max = 100, message = "Theater name must not exceed 100 characters")
    private String nombre;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacidad;
}
