package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShiftRequestDTO {
    @NotBlank
    private String employeeName;
    @NotBlank
    private String position;
    @NotNull
    private LocalDate shiftDate;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private String notes;
    private String status;
}
