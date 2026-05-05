package com.cine.demo.mapper;

import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.model.*;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.SeatType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScreeningMapperTest {

    private ScreeningMapper mapper;

    @BeforeEach
    void setUp() {
        // Construimos el mapper de proyección con sus dependencias reales
        // (no-mock) porque son funciones puras: simplifica el test.
        mapper = new ScreeningMapper(new MovieMapper(), new TheaterMapper(), new SeatMapper());
    }

    /**
     * Verifica el mapeo de una proyección a su DTO.
     * Importante: el campo "completo" es un derivado (asientosDisponibles == 0).
     * Aquí hay 3 asientos disponibles, por lo que completo = false.
     */
    @Test
    void toResponseDto_marksCompletoFalse_whenSeatsAvailable() {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").durationMin(148).ageRating(AgeRating.TWELVE).build();
        Theater theater = Theater.builder().id(2L).nombre("Sala 1").capacidad(100).build();
        Screening screening = Screening.builder()
                .id(10L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.of(2030, 1, 1, 20, 0))
                .precioBase(BigDecimal.valueOf(8.5))
                .asientosDisponibles(3).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getMovie().getTitle()).isEqualTo("Inception");
        assertThat(dto.getTheater().getNombre()).isEqualTo("Sala 1");
        assertThat(dto.getAsientosDisponibles()).isEqualTo(3);
        assertThat(dto.isCompleto()).isFalse();
        assertThat(dto.getPrecioBase()).isEqualByComparingTo("8.5");
    }

    /**
     * Caso límite: cuando asientosDisponibles = 0, "completo" debe ser true.
     * Este flag lo usa el frontend para mostrar la sala como agotada.
     */
    @Test
    void toResponseDto_marksCompletoTrue_whenZeroSeatsAvailable() {
        Movie movie = Movie.builder().id(1L).title("X").durationMin(90).build();
        Theater theater = Theater.builder().id(2L).nombre("Y").capacidad(10).build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now()).precioBase(BigDecimal.TEN)
                .asientosDisponibles(0).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.isCompleto()).isTrue();
    }

    /**
     * Mapeo de un asiento de proyección (estado de reserva).
     * Comprueba que el id de la proyección y el sub-DTO del asiento se incluyen.
     */
    @Test
    void toScreeningSeatResponseDto_includesScreeningIdAndSeatInfo() {
        Theater theater = Theater.builder().id(1L).nombre("Sala 1").build();
        Seat seat = Seat.builder().id(7L).theater(theater)
                .fila("A").numero(5).tipo(SeatType.STANDARD).build();
        Screening screening = Screening.builder().id(50L).theater(theater)
                .fechaHora(LocalDateTime.now()).precioBase(BigDecimal.ONE).build();
        ScreeningSeat ss = ScreeningSeat.builder()
                .id(99L).screening(screening).seat(seat).ocupado(true).build();

        ScreeningSeatResponseDTO dto = mapper.toScreeningSeatResponseDto(ss);

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getScreeningId()).isEqualTo(50L);
        assertThat(dto.getSeat().getId()).isEqualTo(7L);
        assertThat(dto.getSeat().getFila()).isEqualTo("A");
        assertThat(dto.isOcupado()).isTrue();
    }
}
