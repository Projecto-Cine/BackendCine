package com.cine.demo.dto.request;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record UpdateShiftRequestDTO(
        Long employeeId,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        String notes,
        String status
) {}
