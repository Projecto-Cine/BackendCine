package com.cine.demo.mapper;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.model.Seat;
import com.cine.demo.model.enums.SeatType;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponseDTO toResponseDto(Seat seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .theaterId(seat.getTheater().getId())
                .row(seat.getRow())
                .number(seat.getNumber())
                .type(seat.getType() != null ? seat.getType().name() : "STANDARD")
                .build();
    }

    public void updateEntityFromDto(UpdateSeatRequestDTO dto, Seat seat) {
        if (dto.row() != null) seat.setRow(dto.row());
        if (dto.number() != null) seat.setNumber(dto.number());
        if (dto.type() != null) seat.setType(SeatType.valueOf(dto.type()));
    }
}
