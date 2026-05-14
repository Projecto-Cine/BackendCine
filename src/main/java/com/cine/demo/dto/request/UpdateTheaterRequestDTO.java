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
public class UpdateTheaterRequestDTO {
    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
