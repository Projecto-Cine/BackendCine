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
                .fila(seat.getFila())
                .numero(seat.getNumero())
                .tipo(seat.getTipo().name())
                .build();
    }

    public void updateEntityFromDto(UpdateSeatRequestDTO dto, Seat seat) {
        if (dto.getFila() != null) seat.setFila(dto.getFila());
        if (dto.getNumero() != null) seat.setNumero(dto.getNumero());
        if (dto.getTipo() != null) seat.setTipo(SeatType.valueOf(dto.getTipo()));
    }
}
