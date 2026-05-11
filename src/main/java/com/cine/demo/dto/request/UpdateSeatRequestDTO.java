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

    @Min(value = 1, message = "Seat number must be at least 1")
    private Integer number;

    private String type;
}
