package com.cine.demo.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShiftRequestDTO {

    private Long employeeId;

    @FutureOrPresent(message = "Shift date must be today or in the future")
    private LocalDate shiftDate;

    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private String status;
}
