package com.cine.demo.screening;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.exception.ScreeningAlreadyPassedException;
import com.cine.demo.exception.ScreeningFullException;
import com.cine.demo.exception.SeatAlreadyTakenException;
import com.cine.demo.mapper.ScreeningMapper;
import com.cine.demo.model.*;
import com.cine.demo.repository.*;
import com.cine.demo.service.impl.ScreeningServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
    @Mock private ScreeningMapper screeningMapper;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    private Movie movie;
    private Theater theater;

    @BeforeEach
    void setUp() {
        movie = Movie.builder().id(1L).title("Inception").durationMin(148).build();
        theater = Theater.builder().id(1L).nombre("Sala 1").capacidad(100).build();
    }

    /**
     * getAll() debe leer todas las proyecciones del repositorio y mapearlas
     * con el ScreeningMapper. Caso de salida feliz.
     */
    @Test
    void getAll_returnsAllScreeningsMapped() {
        Screening screening = Screening.builder().id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN).build();
        ScreeningResponseDTO dto = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningRepository.findAll()).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening)).thenReturn(dto);

        List<ScreeningResponseDTO> result = screeningService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    /**
     * getUpcoming() debe filtrar por fechaHora > now usando findByFechaHoraAfter.
     * Esto evita mostrar proyecciones que ya pasaron.
     */
    @Test
    void getUpcoming_usesRepositoryFilterByFutureDate() {
        Screening screening = Screening.builder().id(2L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(2)).precioBase(BigDecimal.TEN).build();
        when(screeningRepository.findByFechaHoraAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening))
                .thenReturn(ScreeningResponseDTO.builder().id(2L).build());

        List<ScreeningResponseDTO> result = screeningService.getUpcoming();

        assertThat(result).hasSize(1);
        verify(screeningRepository).findByFechaHoraAfter(any(LocalDateTime.class));
    }

    /**
     * getById caso feliz: retorna la proyección encontrada.
     */
    @Test
    void getById_returnsScreening_whenFound() {
        Screening screening = Screening.builder().id(5L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN).build();
        when(screeningRepository.findById(5L)).thenReturn(Optional.of(screening));
        when(screeningMapper.toResponseDto(screening))
                .thenReturn(ScreeningResponseDTO.builder().id(5L).build());

        ScreeningResponseDTO result = screeningService.getById(5L);

        assertThat(result.getId()).isEqualTo(5L);
    }

    /**
     * getById caso de error: lanza ResourceNotFoundException con id.
     */
    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(screeningRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * getByMovie filtra proyecciones por id de película. Útil para
     * mostrar todas las sesiones de "Inception" en cartelera.
     */
    @Test
    void getByMovie_filtersByMovieId() {
        Screening screening = Screening.builder().id(3L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN).build();
        when(screeningRepository.findByMovieId(1L)).thenReturn(List.of(screening));
        when(screeningMapper.toResponseDto(screening))
                .thenReturn(ScreeningResponseDTO.builder().id(3L).build());

        List<ScreeningResponseDTO> result = screeningService.getByMovie(1L);

        assertThat(result).hasSize(1);
        verify(screeningRepository).findByMovieId(1L);
    }

    /**
     * create con fecha en el pasado: lanza ScreeningAlreadyPassedException.
     * Validación de negocio: no se pueden crear sesiones para fechas pasadas.
     */
    @Test
    void create_throwsScreeningAlreadyPassedException_whenDateInPast() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .fechaHora(LocalDateTime.now().minusDays(1))
                .precioBase(BigDecimal.TEN).build();

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    /**
     * create con película inexistente: ResourceNotFoundException.
     */
    @Test
    void create_throwsResourceNotFoundException_whenMovieNotFound() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(99L).theaterId(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN).build();
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * create con sala inexistente: ResourceNotFoundException.
     */
    @Test
    void create_throwsResourceNotFoundException_whenTheaterNotFound() {
        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(99L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    /**
     * create caso feliz: persiste la proyección y genera los ScreeningSeat
     * necesarios para cada asiento de la sala.
     */
    @Test
    void create_persistsScreeningAndGeneratesSeats_whenValid() {
        Seat seatA = Seat.builder().id(1L).theater(theater).fila("A").numero(1).build();
        Seat seatB = Seat.builder().id(2L).theater(theater).fila("A").numero(2).build();
        Screening saved = Screening.builder()
                .id(50L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN)
                .asientosDisponibles(theater.getCapacidad()).build();

        ScreeningRequestDTO dto = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN).build();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));
        when(screeningRepository.save(any(Screening.class))).thenReturn(saved);
        when(seatRepository.findByTheaterId(1L)).thenReturn(List.of(seatA, seatB));
        when(screeningMapper.toResponseDto(saved))
                .thenReturn(ScreeningResponseDTO.builder().id(50L).build());

        ScreeningResponseDTO result = screeningService.create(dto);

        assertThat(result.getId()).isEqualTo(50L);
        verify(screeningSeatRepository).saveAll(any());
    }

    /**
     * update caso feliz: si la nueva fecha es futura, se actualiza.
     */
    @Test
    void update_updatesFieldsAndPersists_whenValid() {
        Screening existing = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN).build();
        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .fechaHora(LocalDateTime.now().plusDays(5))
                .precioBase(BigDecimal.valueOf(15)).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(screeningRepository.save(existing)).thenReturn(existing);
        when(screeningMapper.toResponseDto(existing))
                .thenReturn(ScreeningResponseDTO.builder().id(1L).build());

        screeningService.update(1L, dto);

        assertThat(existing.getPrecioBase()).isEqualByComparingTo("15");
        verify(screeningRepository).save(existing);
    }

    /**
     * update con nueva fecha en pasado: lanza ScreeningAlreadyPassedException.
     */
    @Test
    void update_throwsScreeningAlreadyPassedException_whenNewDateInPast() {
        Screening existing = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now().plusDays(1)).precioBase(BigDecimal.TEN).build();
        UpdateScreeningRequestDTO dto = UpdateScreeningRequestDTO.builder()
                .fechaHora(LocalDateTime.now().minusDays(1)).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> screeningService.update(1L, dto))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    /**
     * delete caso feliz: borra la proyección por id.
     */
    @Test
    void delete_removesScreening_whenExists() {
        when(screeningRepository.existsById(1L)).thenReturn(true);

        screeningService.delete(1L);

        verify(screeningRepository).deleteById(1L);
    }

    /**
     * delete con id inexistente: lanza ResourceNotFoundException.
     */
    @Test
    void delete_throwsResourceNotFoundException_whenScreeningNotFound() {
        when(screeningRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> screeningService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * reserveSeat lanza ScreeningAlreadyPassedException si la proyección
     * ya ha empezado/terminado.
     */
    @Test
    void reserveSeat_throwsScreeningAlreadyPassedException_whenScreeningInPast() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5)
                .fechaHora(LocalDateTime.now().minusHours(1)).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(ScreeningAlreadyPassedException.class);
    }

    /**
     * reserveSeat lanza ScreeningFullException si no quedan asientos.
     */
    @Test
    void reserveSeat_throwsScreeningFullException_whenNoSeatsAvailable() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(0)
                .fechaHora(LocalDateTime.now().plusDays(1)).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(ScreeningFullException.class);
    }

    /**
     * reserveSeat lanza ResourceNotFoundException si el asiento no
     * pertenece a esta proyección.
     */
    @Test
    void reserveSeat_throwsResourceNotFoundException_whenSeatNotInScreening() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5)
                .fechaHora(LocalDateTime.now().plusDays(1)).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    /**
     * reserveSeat lanza SeatAlreadyTakenException si ya está ocupado.
     */
    @Test
    void reserveSeat_throwsSeatAlreadyTakenException_whenAlreadyOccupied() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5)
                .fechaHora(LocalDateTime.now().plusDays(1)).build();
        ScreeningSeat ss = ScreeningSeat.builder().id(1L).screening(screening).ocupado(true).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(ss));

        assertThatThrownBy(() -> screeningService.reserveSeat(1L, 1L))
                .isInstanceOf(SeatAlreadyTakenException.class);
    }

    /**
     * reserveSeat caso feliz: marca asiento ocupado y decrementa el contador
     * de asientos disponibles en la proyección.
     */
    @Test
    void reserveSeat_marksSeatOccupied_andDecrementsAvailable() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(5)
                .fechaHora(LocalDateTime.now().plusDays(1))
                .precioBase(BigDecimal.TEN).build();
        ScreeningSeat ss = ScreeningSeat.builder().id(1L).screening(screening).ocupado(false).build();
        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(ss));
        when(screeningSeatRepository.save(ss)).thenReturn(ss);
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toScreeningSeatResponseDto(ss)).thenReturn(null);

        screeningService.reserveSeat(1L, 1L);

        assertThat(ss.isOcupado()).isTrue();
        assertThat(screening.getAsientosDisponibles()).isEqualTo(4);
    }

    /**
     * releaseSeat caso feliz: marca asiento desocupado y aumenta el contador.
     * Útil al cancelar una compra.
     */
    @Test
    void releaseSeat_marksSeatFree_andIncrementsAvailable() {
        Screening screening = Screening.builder()
                .id(1L).asientosDisponibles(3)
                .fechaHora(LocalDateTime.now().plusDays(1)).build();
        ScreeningSeat ss = ScreeningSeat.builder()
                .id(1L).screening(screening).ocupado(true).build();
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 1L)).thenReturn(Optional.of(ss));
        when(screeningSeatRepository.save(ss)).thenReturn(ss);
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningMapper.toScreeningSeatResponseDto(ss)).thenReturn(null);

        screeningService.releaseSeat(1L, 1L);

        assertThat(ss.isOcupado()).isFalse();
        assertThat(screening.getAsientosDisponibles()).isEqualTo(4);
    }

    /**
     * releaseSeat con asiento inexistente lanza ResourceNotFoundException.
     */
    @Test
    void releaseSeat_throwsResourceNotFoundException_whenSeatNotInScreening() {
        when(screeningSeatRepository.findByScreeningIdAndSeatId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> screeningService.releaseSeat(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
