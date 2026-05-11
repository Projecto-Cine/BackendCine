package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.exception.SeatAlreadyTakenException;
import com.cine.demo.exception.ScreeningAlreadyPassedException;
import com.cine.demo.exception.ScreeningFullException;
import com.cine.demo.mapper.ScreeningMapper;
import com.cine.demo.model.Movie;
import com.cine.demo.model.Screening;
import com.cine.demo.model.ScreeningSeat;
import com.cine.demo.model.Theater;
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
        return screeningRepository.findByStartDatetimeAfter(LocalDateTime.now()).stream()
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
        if (!dto.startDatetime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("Screening date must be in the future");
        }
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + dto.movieId()));
        Theater theater = theaterRepository.findById(dto.theaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + dto.theaterId()));

        LocalDateTime endDatetime = dto.startDatetime().plusMinutes(movie.getDurationMin());

        Screening screening = Screening.builder()
                .movie(movie)
                .theater(theater)
                .startDatetime(dto.startDatetime())
                .endDatetime(endDatetime)
                .basePrice(dto.basePrice())
                .occupiedSeats(0)
                .full(false)
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
        if (dto.startDatetime() != null && !dto.startDatetime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("Updated screening date must be in the future");
        }
        if (dto.startDatetime() != null) {
            screening.setStartDatetime(dto.startDatetime());
            screening.setEndDatetime(dto.startDatetime().plusMinutes(screening.getMovie().getDurationMin()));
        }
        if (dto.basePrice() != null) screening.setBasePrice(dto.basePrice());
        return screeningMapper.toResponseDto(screeningRepository.save(screening));
    }

    @Override
    public void delete(Long id) {
        if (!screeningRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screening not found with id: " + id);
        }
        screeningRepository.deleteById(id);
    }

    @Override
    public ScreeningSeatResponseDTO reserveSeat(Long screeningId, Long seatId) {
        Screening screening = findOrThrow(screeningId);
        if (!screening.getStartDatetime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("This screening has already ended");
        }
        if (screening.isFull()) {
            throw new ScreeningFullException("No seats available for this screening");
        }
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found in this screening"));
        if (screeningSeat.isOccupied()) {
            throw new SeatAlreadyTakenException("Seat is already taken");
        }
        screeningSeat.setOccupied(true);
        screeningSeatRepository.save(screeningSeat);

        int newOccupied = screening.getOccupiedSeats() + 1;
        screening.setOccupiedSeats(newOccupied);
        screening.setFull(newOccupied >= screening.getTheater().getCapacity());
        screeningRepository.save(screening);

        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    @Override
    public ScreeningSeatResponseDTO releaseSeat(Long screeningId, Long seatId) {
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found in this screening"));
        Screening screening = screeningSeat.getScreening();
        screeningSeat.setOccupied(false);
        screeningSeatRepository.save(screeningSeat);

        int newOccupied = Math.max(0, screening.getOccupiedSeats() - 1);
        screening.setOccupiedSeats(newOccupied);
        screening.setFull(false);
        screeningRepository.save(screening);

        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    private Screening findOrThrow(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found with id: " + id));
    }
}
