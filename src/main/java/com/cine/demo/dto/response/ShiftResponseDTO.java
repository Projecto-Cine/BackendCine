package com.cine.demo.dto.response;

import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record ShiftResponseDTO(
        Long id,
        Long employeeId,
        String employeeName,
        String employeeEmail,
        String employeeRole,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        String notes,
        String status,
        LocalDateTime createdAt
) {}
