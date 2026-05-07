package com.cine.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShiftRequestDTO {

    @NotNull(message = "El trabajador es obligatorio")
    private Long employeeId;

    @NotNull(message = "La fecha del turno es obligatoria")
    private LocalDate shiftDate;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;

    private String notes;
    private String status;
}
