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
                .type(seat.getType().name())
                .build();
    }

    public void updateEntityFromDto(UpdateSeatRequestDTO dto, Seat seat) {
        if (dto.getRow() != null) seat.setRow(dto.getRow());
        if (dto.getNumber() != null) seat.setNumber(dto.getNumber());
        if (dto.getType() != null) seat.setType(SeatType.valueOf(dto.getType()));
    }
}
