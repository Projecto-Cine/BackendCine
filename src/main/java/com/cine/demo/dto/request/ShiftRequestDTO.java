package com.cine.demo.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftRequestDTO(
        @NotNull(message = "Employee is required")
        Long employeeId,

        @NotNull(message = "Shift date is required")
        @FutureOrPresent(message = "Shift date must be today or in the future")
        LocalDate shiftDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        String notes,
        String status
) {}
