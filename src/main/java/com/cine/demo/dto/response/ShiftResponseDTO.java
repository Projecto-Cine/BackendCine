package com.cine.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class ShiftResponseDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeeRole;
    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private String status;
    private LocalDateTime createdAt;
}
