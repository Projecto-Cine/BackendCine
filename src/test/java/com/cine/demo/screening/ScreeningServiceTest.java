package com.cine.demo.screening;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
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
}
