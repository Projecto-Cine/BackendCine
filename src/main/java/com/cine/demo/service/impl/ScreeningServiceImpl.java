package com.cine.demo.service.impl;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.ConflictException;
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
import com.cine.demo.repository.PurchaseRepository;
import com.cine.demo.repository.ScreeningRepository;
import com.cine.demo.repository.ScreeningSeatRepository;
import com.cine.demo.repository.SeatRepository;
import com.cine.demo.repository.TheaterRepository;
import com.cine.demo.repository.TicketRepository;
import com.cine.demo.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Transactional
public class ScreeningServiceImpl implements ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final ScreeningSeatRepository screeningSeatRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final PurchaseRepository purchaseRepository;
    private final TicketRepository ticketRepository;
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
    public List<ScreeningResponseDTO> getByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return screeningRepository.findByDate(start, end).stream()
                .map(screeningMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponseDTO> getUpcoming() {
        return screeningRepository.findByStartTimeAfter(LocalDateTime.now()).stream()
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
        if (!dto.startTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("Screening date must be in the future");
        }
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + dto.movieId()));
        Theater theater = theaterRepository.findById(dto.theaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with id: " + dto.theaterId()));

        LocalDateTime endDatetime = dto.startTime().plusMinutes(movie.getDurationMin());

        if (screeningRepository.existsConflict(theater.getId(), dto.startTime(), endDatetime, -1L)) {
            throw new ConflictException("La sala ya está ocupada en ese horario.");
        }

        Screening screening = Screening.builder()
                .movie(movie)
                .theater(theater)
                .startTime(dto.startTime())
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
        if (dto.startTime() != null && !dto.startTime().isAfter(LocalDateTime.now())) {
            throw new ScreeningAlreadyPassedException("New screening date must be in the future");
        }
        if (dto.startTime() != null) {
            LocalDateTime newEnd = dto.startTime().plusMinutes(screening.getMovie().getDurationMin());
            if (screeningRepository.existsConflict(screening.getTheater().getId(), dto.startTime(), newEnd, id)) {
                throw new ConflictException("La sala ya está ocupada en ese horario.");
            }
            screening.setStartTime(dto.startTime());
            screening.setEndDatetime(newEnd);
        }
        if (dto.basePrice() != null) screening.setBasePrice(dto.basePrice());
        return screeningMapper.toResponseDto(screeningRepository.save(screening));
    }

    @Override
    public void delete(Long id) {
        if (!screeningRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screening not found with id: " + id);
        }
        ticketRepository.deleteByScreeningId(id);
        purchaseRepository.deleteAll(purchaseRepository.findByScreeningId(id));
        screeningSeatRepository.deleteByScreeningId(id);
        screeningRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningSeatResponseDTO> getSeats(Long screeningId) {
        return screeningSeatRepository.findByScreeningId(screeningId).stream()
                .map(screeningMapper::toScreeningSeatResponseDto)
                .toList();
    }

    private static final int RESERVATION_MINUTES = 3;

    @Override
    public ScreeningSeatResponseDTO tempReserveSeat(Long screeningId, Long seatId) {
        Screening screening = findOrThrow(screeningId);
        if (!screening.getStartTime().isAfter(now())) {
            throw new ScreeningAlreadyPassedException("This screening has already ended");
        }
        if (screening.isFull()) {
            throw new ScreeningFullException("No seats available for this screening");
        }
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found in this screening"));

        if (screeningSeat.isEffectivelyTaken()) {
            throw new SeatAlreadyTakenException("Seat " + screeningSeat.getSeat().getRow()
                    + screeningSeat.getSeat().getNumber() + " is already occupied or reserved");
        }

        screeningSeat.setReservedUntil(now().plusMinutes(RESERVATION_MINUTES));
        screeningSeatRepository.save(screeningSeat);

        int newOccupied = screening.getOccupiedSeats() + 1;
        screening.setOccupiedSeats(newOccupied);
        screening.setFull(newOccupied >= screening.getTheater().getCapacity());
        screeningRepository.save(screening);

        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    @Override
    public ScreeningSeatResponseDTO reserveSeat(Long screeningId, Long seatId) {
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found in this screening"));

        if (screeningSeat.isOccupied()) {
            throw new SeatAlreadyTakenException("Seat is already permanently occupied");
        }

        boolean alreadyCounted = screeningSeat.getReservedUntil() != null;
        screeningSeat.setOccupied(true);
        screeningSeat.setReservedUntil(null);
        screeningSeatRepository.save(screeningSeat);

        if (!alreadyCounted) {
            Screening screening = screeningSeat.getScreening();
            int newOccupied = screening.getOccupiedSeats() + 1;
            screening.setOccupiedSeats(newOccupied);
            screening.setFull(newOccupied >= screening.getTheater().getCapacity());
            screeningRepository.save(screening);
        }

        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    @Override
    public ScreeningSeatResponseDTO releaseSeat(Long screeningId, Long seatId) {
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findByScreeningIdAndSeatId(screeningId, seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found in this screening"));
        Screening screening = screeningSeat.getScreening();

        boolean wasTaken = screeningSeat.isEffectivelyTaken();
        screeningSeat.setOccupied(false);
        screeningSeat.setReservedUntil(null);
        screeningSeatRepository.save(screeningSeat);

        if (wasTaken) {
            int newOccupied = Math.max(0, screening.getOccupiedSeats() - 1);
            screening.setOccupiedSeats(newOccupied);
            screening.setFull(false);
            screeningRepository.save(screening);
        }

        return screeningMapper.toScreeningSeatResponseDto(screeningSeat);
    }

    @Override
    public void releaseExpiredReservations() {
        List<ScreeningSeat> expired = screeningSeatRepository.findByReservedUntilBefore(now());
        for (ScreeningSeat ss : expired) {
            Screening screening = ss.getScreening();
            ss.setReservedUntil(null);
            screeningSeatRepository.save(ss);
            int newOccupied = Math.max(0, screening.getOccupiedSeats() - 1);
            screening.setOccupiedSeats(newOccupied);
            screening.setFull(false);
            screeningRepository.save(screening);
        }
    }

    @Override
    public List<ScreeningSeatResponseDTO> syncSeats(Long screeningId) {
        Screening screening = findOrThrow(screeningId);
        List<Long> existingSeatIds = screeningSeatRepository.findByScreeningId(screeningId).stream()
                .map(ss -> ss.getSeat().getId())
                .toList();
        List<ScreeningSeat> newSeats = seatRepository.findByTheaterId(screening.getTheater().getId()).stream()
                .filter(seat -> !existingSeatIds.contains(seat.getId()))
                .map(seat -> ScreeningSeat.builder()
                        .screening(screening)
                        .seat(seat)
                        .occupied(false)
                        .build())
                .toList();
        if (!newSeats.isEmpty()) {
            screeningSeatRepository.saveAll(newSeats);
        }
        return screeningSeatRepository.findByScreeningId(screeningId).stream()
                .map(screeningMapper::toScreeningSeatResponseDto)
                .toList();
    }

    private Screening findOrThrow(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found with id: " + id));
    }
}
