package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ShiftRequestDTO(
        @NotNull(message = "Employee is required")
        Long employeeId,
        @NotNull(message = "Shift date is required")
        LocalDate shiftDate,
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        @NotNull(message = "End time is required")
        LocalTime endTime,
        String notes,
        String status
) {}
