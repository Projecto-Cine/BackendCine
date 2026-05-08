package com.cine.demo.service.impl;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.SeatMapper;
import com.cine.demo.model.Seat;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.SeatType;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;
    private final SeatMapper seatMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getAll() {
        return seatRepository.findAll().stream()
                .map(seatMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getByTheater(Long theaterId) {
        return seatRepository.findByTheaterId(theaterId).stream()
                .map(seatMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatResponseDTO getById(Long id) {
        return seatMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    public SeatResponseDTO create(SeatRequestDTO dto) {
        if (seatRepository.existsByTheaterIdAndFilaAndNumero(dto.getTheaterId(), dto.getFila(), dto.getNumero())) {
            throw new ConflictException("Ya existe el asiento " + dto.getFila() + dto.getNumero() + " en esa sala");
        }
        Theater theater = theaterRepository.findById(dto.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + dto.getTheaterId()));
        Seat seat = Seat.builder()
                .theater(theater)
                .fila(dto.getFila())
                .numero(dto.getNumero())
                .tipo(SeatType.valueOf(dto.getTipo()))
                .build();
        return seatMapper.toResponseDto(seatRepository.save(seat));
    }

    @Override
    public SeatResponseDTO update(Long id, UpdateSeatRequestDTO dto) {
        Seat seat = findOrThrow(id);
        seatMapper.updateEntityFromDto(dto, seat);
        return seatMapper.toResponseDto(seatRepository.save(seat));
    }

    @Override
    public void delete(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asiento no encontrado con id: " + id);
        }
        seatRepository.deleteById(id);
    }

    private Seat findOrThrow(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id: " + id));
    }
}
