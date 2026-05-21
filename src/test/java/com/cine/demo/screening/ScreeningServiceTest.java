package com.cine.demo.screening;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.exception.SeatAlreadyTakenException;
import com.cine.demo.exception.ScreeningAlreadyPassedException;
import com.cine.demo.exception.ScreeningFullException;
import com.cine.demo.mapper.ScreeningMapper;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.repository.*;
import com.cine.demo.service.impl.ScreeningServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock private ScreeningRepository screeningRepository;
    @Mock private ScreeningSeatRepository screeningSeatRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private TheaterRepository theaterRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private PurchaseRepository purchaseRepository;
    @Mock private ScreeningMapper screeningMapper;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    private Movie sampleMovie() {
        return Movie.builder().id(1L).title("Inception").durationMin(90)
                .genre("Drama").ageRating(AgeRating.ALL).build();
    }

    private Theater sampleTheater() {
        return Theater.builder().id(1L).name("Sala 1").capacity(10).build();
    }

    private Screening sampleScreening() {
        return Screening.builder()
                .id(1L).movie(sampleMovie()).theater(sampleTheater())
                .occupiedSeats(5).full(false)
                .startTime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1).plusMinutes(90))
                .basePrice(BigDecimal.TEN)
                .build();
    }

    @Test
    void create_throwsScreeningAlreadyPassedException_whenDateInPast() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .startTime(LocalDateTime.now().minusDays(1))
                .basePrice(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    @Test
    void create_throwsResourceNotFoundException_whenMovieNotFound() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(99L).theaterId(1L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(BigDecimal.TEN)
                .build();
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesScreeningAndSeats_whenValid() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(BigDecimal.TEN)
                .build();
        Movie movie = sampleMovie();
        Theater theater = sampleTheater();
        Screening saved = sampleScreening();
        ScreeningResponseDTO responseDTO = ScreeningResponseDTO.builder().id(1L).build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(screeningRepository.save(any())).thenReturn(saved);
        when(seatRepository.findByTheaterId(1L)).thenReturn(List.of(
                Seat.builder().id(1L).theater(theater).row("A").number(1).build(),
                Seat.builder().id(2L).theater(theater).row("A").number(2).build()));
        when(screeningMapper.toResponseDto(saved)).thenReturn(responseDTO);

        ScreeningResponseDTO result = screeningService.create(dto);

        assertThat(result.id()).isEqualTo(1L);
        verify(screeningSeatRepository).saveAll(any());
    }

    @Test
    void getAll_returnsMappedList() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findAll()).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        List<ScreeningResponseDTO> result = screeningService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void getAll_returnsEmptyList_whenNoScreenings() {
        when(screeningRepository.findAll()).thenReturn(List.of());

        assertThat(screeningService.getAll()).isEmpty();
    }

    @Test
    void getById_returnsDto_whenFound() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        ScreeningResponseDTO result = screeningService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(screeningRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(screeningRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> screeningService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_callsDeleteById_whenFound() {
        when(screeningRepository.existsById(1L)).thenReturn(true);
        when(purchaseRepository.findByScreeningId(1L)).thenReturn(List.of());

        screeningService.delete(1L);

        verify(screeningRepository).deleteById(1L);
        verify(ticketRepository).deleteByScreeningId(1L);
        verify(screeningSeatRepository).deleteByScreeningId(1L);
    }

    @Test
    void reserveSeat_throwsSeatAlreadyTakenException_whenAlreadyOccupied() {
        Screening screening = sampleScreening();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(true).build();
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L))
                .thenReturn(Optional.of(screeningSeat));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(SeatAlreadyTakenException.class);
    }

    @Test
    void reserveSeat_setsOccupiedAndNullsReservedUntil_whenSuccessful() {
        Theater theater = sampleTheater();
        Movie movie = sampleMovie();
        Screening screening = Screening.builder()
                .id(1L).occupiedSeats(5).movie(movie).theater(theater)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(BigDecimal.TEN).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(false).reservedUntil(null).build();

        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L))
                .thenReturn(Optional.of(screeningSeat));
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(null);

        screeningService.reserveSeat(1L, 1L);

        assertThat(screeningSeat.isOccupied()).isTrue();
        assertThat(screeningSeat.getReservedUntil()).isNull();
        assertThat(screening.getOccupiedSeats()).isEqualTo(6);
    }

    @Test
    void tempReserveSeat_throwsScreeningFullException_whenNoSeatsAvailable() {
        Screening screening = Screening.builder()
                .id(1L).full(true)
                .startTime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1).plusMinutes(90))
                .build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> screeningService.tempReserveSeat(1L, 1L))
                .isInstanceOf(ScreeningFullException.class);
    }

    @Test
    void tempReserveSeat_throwsScreeningAlreadyPassedException_whenPast() {
        Screening screening = Screening.builder()
                .id(1L).full(false)
                .startTime(LocalDateTime.now().minusDays(1))
                .endDatetime(LocalDateTime.now().minusHours(1))
                .build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> screeningService.tempReserveSeat(1L, 1L))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    @Test
    void releaseSeat_throwsResourceNotFoundException_whenSeatNotFound() {
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.releaseSeat(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── create: theater not found ──────────────────────────────────────────────

    @Test
    void create_throwsResourceNotFoundException_whenTheaterNotFound() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(99L)
                .startTime(LocalDateTime.now().plusDays(1))
                .basePrice(BigDecimal.TEN).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(sampleMovie()));
        when(theaterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── getByDate, getUpcoming, getByMovie ─────────────────────────────────────

    @Test
    void getByDate_returnsMappedList() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findByDate(any(), any())).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        List<ScreeningResponseDTO> result = screeningService.getByDate(LocalDate.now());

        assertThat(result).hasSize(1);
    }

    @Test
    void getUpcoming_returnsMappedList() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findByStartTimeAfter(any())).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        assertThat(screeningService.getUpcoming()).hasSize(1);
    }

    @Test
    void getByMovie_returnsMappedList() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findByMovieId(1L)).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        assertThat(screeningService.getByMovie(1L)).hasSize(1);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(screeningRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.update(99L, UpdateScreeningRequestDTO.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_throwsScreeningAlreadyPassedException_whenNewDateInPast() {
        Screening screening = sampleScreening();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .startTime(LocalDateTime.now().minusDays(1)).build();

        assertThatThrownBy(() -> screeningService.update(1L, dto))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    @Test
    void update_updatesStartTimeAndBasePrice_whenValid() {
        Screening screening = sampleScreening();
        ScreeningResponseDTO responseDTO = ScreeningResponseDTO.builder().id(1L).build();
        LocalDateTime newTime = LocalDateTime.now().plusDays(3);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toResponseDto(screening)).thenReturn(responseDTO);

        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .startTime(newTime).basePrice(BigDecimal.valueOf(15)).build();
        ScreeningResponseDTO result = screeningService.update(1L, dto);

        assertThat(screening.getStartTime()).isEqualTo(newTime);
        assertThat(screening.getBasePrice()).isEqualByComparingTo("15");
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void update_updatesOnlyBasePrice_whenStartTimeIsNull() {
        Screening screening = sampleScreening();
        LocalDateTime originalTime = screening.getStartTime();
        ScreeningResponseDTO responseDTO = ScreeningResponseDTO.builder().id(1L).build();

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toResponseDto(screening)).thenReturn(responseDTO);

        screeningService.update(1L, UpdateScreeningRequestDTO.builder().basePrice(BigDecimal.valueOf(12)).build());

        assertThat(screening.getStartTime()).isEqualTo(originalTime);
        assertThat(screening.getBasePrice()).isEqualByComparingTo("12");
    }

    // ── getSeats ──────────────────────────────────────────────────────────────

    @Test
    void getSeats_returnsMappedList() {
        Screening screening = sampleScreening();
        ScreeningSeat screeningSeat = ScreeningSeat.builder().id(1L).screening(screening).build();
        ScreeningSeatResponseDTO responseDTO = ScreeningSeatResponseDTO.builder().id(1L).build();

        when(screeningSeatRepository.findByScreeningId(1L)).thenReturn(List.of(screeningSeat));
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(responseDTO);

        assertThat(screeningService.getSeats(1L)).hasSize(1);
    }

    // ── tempReserveSeat: additional branches ──────────────────────────────────

    @Test
    void tempReserveSeat_throwsResourceNotFoundException_whenSeatNotInScreening() {
        Screening screening = Screening.builder()
                .id(1L).startTime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1).plusMinutes(90)).full(false).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.tempReserveSeat(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void tempReserveSeat_throwsSeatAlreadyTakenException_whenSeatOccupied() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).theater(theater).startTime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1).plusMinutes(90)).full(false).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("A").number(1).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).seat(seat).occupied(true).build();

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));

        assertThatThrownBy(() -> screeningService.tempReserveSeat(1L, 1L))
                .isInstanceOf(SeatAlreadyTakenException.class);
    }

    @Test
    void tempReserveSeat_setsReservationAndIncrementsCount_whenAvailable() {
        Theater theater = sampleTheater();
        Movie movie = sampleMovie();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.now().plusDays(1))
                .endDatetime(LocalDateTime.now().plusDays(1).plusMinutes(90))
                .occupiedSeats(0).full(false).basePrice(BigDecimal.TEN).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("A").number(1).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).seat(seat).occupied(false).reservedUntil(null).build();
        ScreeningSeatResponseDTO responseDTO = ScreeningSeatResponseDTO.builder().id(1L).build();

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(responseDTO);

        ScreeningSeatResponseDTO result = screeningService.tempReserveSeat(1L, 1L);

        assertThat(screeningSeat.getReservedUntil()).isNotNull();
        assertThat(screening.getOccupiedSeats()).isEqualTo(1);
        assertThat(result.id()).isEqualTo(1L);
    }

    // ── reserveSeat: alreadyCounted branch ────────────────────────────────────

    @Test
    void reserveSeat_doesNotIncrementOccupied_whenSeatWasTempReserved() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).occupiedSeats(5).movie(sampleMovie()).theater(theater)
                .startTime(LocalDateTime.now().plusDays(1)).basePrice(BigDecimal.TEN).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(false)
                .reservedUntil(LocalDateTime.now().plusMinutes(2)).build();

        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(null);

        screeningService.reserveSeat(1L, 1L);

        assertThat(screeningSeat.isOccupied()).isTrue();
        assertThat(screeningSeat.getReservedUntil()).isNull();
        assertThat(screening.getOccupiedSeats()).isEqualTo(5);
        verify(screeningRepository, never()).save(any());
    }

    // ── releaseSeat: success paths ────────────────────────────────────────────

    @Test
    void releaseSeat_decrementsOccupied_whenSeatWasTaken() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).occupiedSeats(3).full(true).movie(sampleMovie()).theater(theater)
                .basePrice(BigDecimal.TEN).startTime(LocalDateTime.now().plusDays(1)).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(true).build();

        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(null);

        screeningService.releaseSeat(1L, 1L);

        assertThat(screeningSeat.isOccupied()).isFalse();
        assertThat(screening.getOccupiedSeats()).isEqualTo(2);
        assertThat(screening.isFull()).isFalse();
    }

    @Test
    void releaseSeat_doesNotDecrementOccupied_whenSeatWasNotTaken() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).occupiedSeats(3).movie(sampleMovie()).theater(theater)
                .basePrice(BigDecimal.TEN).startTime(LocalDateTime.now().plusDays(1)).build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(false).reservedUntil(null).build();

        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(null);

        screeningService.releaseSeat(1L, 1L);

        assertThat(screening.getOccupiedSeats()).isEqualTo(3);
        verify(screeningRepository, never()).save(any());
    }

    // ── releaseExpiredReservations ────────────────────────────────────────────

    @Test
    void releaseExpiredReservations_clearsExpiredAndDecrementsCount() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).occupiedSeats(2).full(true).movie(sampleMovie()).theater(theater)
                .basePrice(BigDecimal.TEN).startTime(LocalDateTime.now().plusDays(1)).build();
        ScreeningSeat expired = ScreeningSeat.builder()
                .id(1L).screening(screening).occupied(false)
                .reservedUntil(LocalDateTime.now().minusMinutes(1)).build();

        when(screeningSeatRepository.findByReservedUntilBefore(any())).thenReturn(List.of(expired));
        when(screeningSeatRepository.save(expired)).thenReturn(expired);
        when(screeningRepository.save(screening)).thenReturn(screening);

        screeningService.releaseExpiredReservations();

        assertThat(expired.getReservedUntil()).isNull();
        assertThat(screening.getOccupiedSeats()).isEqualTo(1);
        assertThat(screening.isFull()).isFalse();
    }

    @Test
    void releaseExpiredReservations_doesNothing_whenNoExpiredReservations() {
        when(screeningSeatRepository.findByReservedUntilBefore(any())).thenReturn(List.of());

        screeningService.releaseExpiredReservations();

        verify(screeningSeatRepository, never()).save(any());
    }

    // ── syncSeats ─────────────────────────────────────────────────────────────

    @Test
    void syncSeats_addsNewSeats_whenTheaterHasMoreSeats() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).movie(sampleMovie()).theater(theater)
                .startTime(LocalDateTime.now().plusDays(1)).basePrice(BigDecimal.TEN).build();
        Seat existingSeat = Seat.builder().id(1L).theater(theater).row("A").number(1).build();
        Seat newSeat = Seat.builder().id(2L).theater(theater).row("A").number(2).build();
        ScreeningSeat existingScreeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).seat(existingSeat).build();
        ScreeningSeatResponseDTO responseDTO = ScreeningSeatResponseDTO.builder().id(1L).build();

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningId(1L))
                .thenReturn(List.of(existingScreeningSeat))
                .thenReturn(List.of(existingScreeningSeat));
        when(seatRepository.findByTheaterId(1L)).thenReturn(List.of(existingSeat, newSeat));
        when(screeningSeatRepository.saveAll(any())).thenReturn(List.of());
        when(screeningMapper.toScreeningSeatResponseDto(any())).thenReturn(responseDTO);

        List<ScreeningSeatResponseDTO> result = screeningService.syncSeats(1L);

        verify(screeningSeatRepository).saveAll(any());
        assertThat(result).isNotNull();
    }

    @Test
    void syncSeats_doesNotSaveAll_whenNoNewSeats() {
        Theater theater = sampleTheater();
        Screening screening = Screening.builder()
                .id(1L).movie(sampleMovie()).theater(theater)
                .startTime(LocalDateTime.now().plusDays(1)).basePrice(BigDecimal.TEN).build();
        Seat seat = Seat.builder().id(1L).theater(theater).row("A").number(1).build();
        ScreeningSeat existingScreeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).seat(seat).build();

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningId(1L)).thenReturn(List.of(existingScreeningSeat));
        when(seatRepository.findByTheaterId(1L)).thenReturn(List.of(seat));

        screeningService.syncSeats(1L);

        verify(screeningSeatRepository, never()).saveAll(any());
    }
}
