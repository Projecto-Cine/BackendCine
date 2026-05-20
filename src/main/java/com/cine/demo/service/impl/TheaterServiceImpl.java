package com.cine.demo.service.impl;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.TheaterMapper;
import com.cine.demo.model.Screening;
import com.cine.demo.model.Seat;
import com.cine.demo.model.ScreeningSeat;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.SeatType;
import com.cine.demo.repository.ScreeningRepository;
import com.cine.demo.repository.ScreeningSeatRepository;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final TheaterMapper theaterMapper;

    private static final int SEATS_PER_ROW = 10;

    @Override
    @Transactional(readOnly = true)
    public List<TheaterResponseDTO> getAll() {
        return theaterRepository.findAll().stream()
                .map(theaterMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TheaterResponseDTO getById(Long id) {
        return theaterMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    public TheaterResponseDTO create(TheaterRequestDTO dto) {
        if (theaterRepository.existsByName(dto.name())) {
            throw new ConflictException("A theater already exists with name: " + dto.name());
        }
        Theater theater = theaterMapper.toEntity(dto);
        Theater saved = theaterRepository.save(theater);
        generateSeats(saved);
        return theaterMapper.toResponseDto(saved);
    }

    @Override
    public TheaterResponseDTO update(Long id, UpdateTheaterRequestDTO dto) {
        Theater theater = findOrThrow(id);
        theaterMapper.updateEntityFromDto(dto, theater);
        Theater saved = theaterRepository.save(theater);
        if (dto.capacity() != null) {
            regenerateSeats(saved);
        }
        return theaterMapper.toResponseDto(theaterRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    public void delete(Long id) {
        if (!theaterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Theater not found with id: " + id);
        }
        theaterRepository.deleteById(id);
    }

    private void generateSeats(Theater theater) {
        int capacity = theater.getCapacity();
        int seatCount = 0;
        for (int rowIndex = 0; rowIndex < 26 && seatCount < capacity; rowIndex++) {
            String row = String.valueOf((char) ('A' + rowIndex));
            for (int num = 1; num <= SEATS_PER_ROW && seatCount < capacity; num++) {
                seatRepository.save(Seat.builder()
                        .theater(theater)
                        .row(row)
                        .number(num)
                        .type(SeatType.STANDARD)
                        .build());
                seatCount++;
            }
        }
    }

    private void regenerateSeats(Theater theater) {
        List<Screening> screenings = screeningRepository.findByTheaterId(theater.getId());
        for (Screening screening : screenings) {
            screeningSeatRepository.deleteByScreeningId(screening.getId());
        }
        seatRepository.deleteByTheaterId(theater.getId());
        seatRepository.flush();
        generateSeats(theater);
        List<Seat> newSeats = seatRepository.findByTheaterId(theater.getId());
        if (!screenings.isEmpty() && !newSeats.isEmpty()) {
            List<ScreeningSeat> screeningSeats = screenings.stream()
                    .flatMap(s -> newSeats.stream().map(seat -> ScreeningSeat.builder()
                            .screening(s)
                            .seat(seat)
                            .occupied(false)
                            .build()))
                    .toList();
            screeningSeatRepository.saveAll(screeningSeats);
        }
    }

    private Theater findOrThrow(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + id));
    }
}
