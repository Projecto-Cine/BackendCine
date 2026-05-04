package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.exception.SeatAlreadyTakenException;
import com.cine.demo.exception.ScreeningAlreadyPassedException;
import com.cine.demo.exception.ScreeningFullException;
import com.cine.demo.mapper.ScreeningMapper;
import com.cine.demo.model.Movie;
import com.cine.demo.model.Screening;
import com.cine.demo.model.ScreeningSeat;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.ScreeningStatus;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.repository.ScreeningRepository;
import com.cine.demo.repository.ScreeningSeatRepository;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ScreeningMapper screeningMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponseDTO> getAll() {
        return screeningRepository.findAll().stream()
                .map(screeningMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponseDTO> getUpcoming() {
        return screeningRepository.findByDateTimeAfter(LocalDateTime.now()).stream()
                .map(screeningMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningResponseDTO getById(Long id) {
        return screeningMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponseDTO> getByMovie(Long movieId) {
        return screeningRepository.findByMovieId(movieId).stream()
                .map(screeningMapper::toResponseDto)
                .toList();
    }

    @Override
    public ScreeningResponseDTO create(ScreeningRequestDTO dto) {
        if (!dto.getDateTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("La fecha de la proyección debe ser futura");
        }
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + dto.getMovieId()));
        Theater theater = theaterRepository.findById(dto.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + dto.getTheaterId()));

        Screening screening = Screening.builder()
                .movie(movie)
                .theater(theater)
                .dateTime(dto.getDateTime())
                .basePrice(dto.getBasePrice())
                .availableSeats(theater.getCapacity())
                .build();
        Screening saved = screeningRepository.save(screening);

        List<ScreeningSeat> screeningSeats = seatRepository.findByTheaterId(theater.getId()).stream()
                .map(seat -> ScreeningSeat.builder()
                        .screening(saved)
                        .seat(seat)
                        .occupied(false)
                        .build())
                .toList();
        screeningSeatRepository.saveAll(screeningSeats);

        return screeningMapper.toResponseDto(saved);
    }

    @Override
    public ScreeningResponseDTO update(Long id, UpdateScreeningRequestDTO dto) {
        Screening screening = findOrThrow(id);
        if (dto.getDateTime() != null && !dto.getDateTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("La nueva fecha de la proyección debe ser futura");
        }
        if (dto.getDateTime() != null) screening.setDateTime(dto.getDateTime());
        if (dto.getBasePrice() != null) screening.setBasePrice(dto.getBasePrice());
        if (dto.getStatus() != null) screening.setStatus(ScreeningStatus.valueOf(dto.getStatus()));
        return screeningMapper.toResponseDto(screeningRepository.save(screening));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponseDTO> getSeatsByScreening(Long screeningId) {
        if (!screeningRepository.existsById(screeningId)) {
            throw new ResourceNotFoundException("Proyección no encontrada con id: " + screeningId);
        }
        return screeningSeatRepository.findByScreeningId(screeningId).stream()
                .map(screeningMapper::toSeatStatusDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!screeningRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proyección no encontrada con id: " + id);
        }
        screeningRepository.deleteById(id);
    }

    @Override
    public ScreeningSeatResponseDTO reserveSeat(Long screeningId, Long seatId) {
        Screening screening = findOrThrow(screeningId);
        if (!screening.getDateTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("Esta proyección ya ha finalizado");
        }
        if (screening.getAvailableSeats() == 0) {
            throw new ScreeningFullException("No hay asientos disponibles para esta proyección");
        }
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado en esta proyección"));
        if (screeningSeat.isOccupied()) {
            throw new SeatAlreadyTakenException("El asiento ya está ocupado");
        }
        screeningSeat.setOccupied(true);
        screeningSeatRepository.save(screeningSeat);
        screening.setAvailableSeats(screening.getAvailableSeats() - 1);
        screeningRepository.save(screening);
        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    @Override
    public ScreeningSeatResponseDTO releaseSeat(Long screeningId, Long seatId) {
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado en esta proyección"));
        Screening screening = screeningSeat.getScreening();
        screeningSeat.setOccupied(false);
        screeningSeatRepository.save(screeningSeat);
        screening.setAvailableSeats(screening.getAvailableSeats() + 1);
        screeningRepository.save(screening);
        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    private Screening findOrThrow(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyección no encontrada con id: " + id));
    }
}