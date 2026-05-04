package com.cine.demo.mapper;

import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
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
        return ScreeningResponseDTO.builder()
                .id(screening.getId())
                .movie(movieMapper.toResponseDto(screening.getMovie()))
                .theater(theaterMapper.toResponseDto(screening.getTheater()))
                .dateTime(screening.getDateTime())
                .basePrice(screening.getBasePrice())
                .price(screening.getBasePrice())
                .availableSeats(screening.getAvailableSeats())
                .status(screening.getStatus() != null ? screening.getStatus().name() : "SCHEDULED")
                .full(screening.getAvailableSeats() == 0)
                .createdAt(screening.getCreatedAt())
                .updatedAt(screening.getUpdatedAt())
                .build();
    }

    public ScreeningSeatResponseDTO toScreeningSeatResponseDto(ScreeningSeat screeningSeat) {
        SeatResponseDTO seatDto = seatMapper.toResponseDto(screeningSeat.getSeat());
        seatDto.setStatus(screeningSeat.isOccupied() ? "occupied" : "available");
        return ScreeningSeatResponseDTO.builder()
                .id(screeningSeat.getId())
                .screeningId(screeningSeat.getScreening().getId())
                .seat(seatDto)
                .occupied(screeningSeat.isOccupied())
                .build();
    }

    public SeatResponseDTO toSeatStatusDto(ScreeningSeat ss) {
        SeatResponseDTO dto = seatMapper.toResponseDto(ss.getSeat());
        dto.setStatus(ss.isOccupied() ? "occupied" : "available");
        return dto;
    }
}