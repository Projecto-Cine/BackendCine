package com.cine.demo.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateShiftRequestDTO(
        Long employeeId,

        @FutureOrPresent(message = "Shift date must be today or in the future")
        LocalDate shiftDate,

        LocalTime startTime,
        LocalTime endTime,
        String notes,
        String status
) {}
