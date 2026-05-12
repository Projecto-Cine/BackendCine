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

    @NotNull(message = "Theater ID is required")
    private Long theaterId;

    @NotBlank(message = "Row is required")
    private String row;

    @Min(value = 1, message = "Seat number must be at least 1")
    private int number;

    @NotNull(message = "Seat type is required")
    private String type;
}
