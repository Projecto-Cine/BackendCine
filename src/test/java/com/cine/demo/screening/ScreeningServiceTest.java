package com.cine.demo.screening;

import com.cine.demo.dto.request.ScreeningRequestDTO;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock private ScreeningRepository screeningRepository;
    @Mock private ScreeningSeatRepository screeningSeatRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private TheaterRepository theaterRepository;
    @Mock private ScreeningMapper screeningMapper;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    @Test
    void create_throwsScreeningAlreadyPassedException_whenDateInPast() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .fechaHora(LocalDateTime.now().minusDays(1))
                .precioBase(BigDecimal.TEN)
                .build();

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    @Test
    void reserveSeat_throwsScreeningFullException_whenNoSeatsAvailable() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(0)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(ScreeningFullException.class);
    }

    @Test
    void reserveSeat_throwsSeatAlreadyTakenException_whenAlreadyOccupied() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).ocupado(true).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(SeatAlreadyTakenException.class);
    }

    @Test
    void reserveSeat_decrementsAvailableSeats_whenSuccessful() {
        Theater theater = Theater.builder().id(1L).capacidad(10).nombre("Sala 1").build();
        Movie movie = Movie.builder().id(1L).titulo("Test").duracionMin(90).genero("Drama").clasificacionEdad("PG").build();
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN)
                .build();
        ScreeningSeat screeningSeat = ScreeningSeat.builder()
                .id(1L).screening(screening).ocupado(false).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(screeningSeat));
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningSeatRepository.save(screeningSeat)).thenReturn(screeningSeat);
        when(screeningMapper.toScreeningSeatResponseDto(screeningSeat)).thenReturn(null);

        screeningService.reserveSeat(1L, 1L);

        org.assertj.core.api.Assertions.assertThat(screening.getAsientosDisponibles()).isEqualTo(4);
    }
}
