package com.cine.demo.mapper;

import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.model.Screening;
import com.cine.demo.model.ScreeningSeat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScreeningMapper {

    private final MovieMapper movieMapper;
    private final TheaterMapper theaterMapper;
    private final SeatMapper seatMapper;

    public ScreeningResponseDTO toResponseDto(Screening screening) {
        int available = screening.getTheater().getCapacidad() - screening.getOccupiedSeats();
        return ScreeningResponseDTO.builder()
                .id(screening.getId())
                .movie(movieMapper.toResponseDto(screening.getMovie()))
                .theater(theaterMapper.toResponseDto(screening.getTheater()))
                .fechaHora(screening.getFechaHora())
                .endDatetime(screening.getEndDatetime())
                .precioBase(screening.getPrecioBase())
                .asientosDisponibles(available)
                .completo(screening.isFull())
                .build();
    }

    public ScreeningSeatResponseDTO toScreeningSeatResponseDto(ScreeningSeat screeningSeat) {
        return ScreeningSeatResponseDTO.builder()
                .id(screeningSeat.getId())
                .screeningId(screeningSeat.getScreening().getId())
                .seat(seatMapper.toResponseDto(screeningSeat.getSeat()))
                .ocupado(screeningSeat.isOcupado())
                .build();
    }
}
