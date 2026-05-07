package com.cine.demo.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateShiftRequestDTO {
    private String employeeName;
    private String position;
    private LocalDate shiftDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private String status;
}
