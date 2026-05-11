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
        mapper = new ScreeningMapper(new MovieMapper(), new TheaterMapper(), new SeatMapper());
    }

    @Test
    void toResponseDto_marksCompletoFalse_whenSeatsAvailable() {
        Movie movie = Movie.builder()
                .id(1L).titulo("Inception").duracionMin(148).clasificacionEdad(AgeRating.TWELVE).build();
        Theater theater = Theater.builder().id(2L).nombre("Sala 1").capacidad(100).build();
        Screening screening = Screening.builder()
                .id(10L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.of(2030, 1, 1, 20, 0))
                .precioBase(BigDecimal.valueOf(8.5))
                .occupiedSeats(3).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getMovie().getTitulo()).isEqualTo("Inception");
        assertThat(dto.getTheater().getNombre()).isEqualTo("Sala 1");
        assertThat(dto.getAsientosDisponibles()).isEqualTo(97);
        assertThat(dto.isCompleto()).isFalse();
        assertThat(dto.getPrecioBase()).isEqualByComparingTo("8.5");
    }

    @Test
    void toResponseDto_marksCompletoTrue_whenZeroSeatsAvailable() {
        Movie movie = Movie.builder().id(1L).titulo("X").duracionMin(90).build();
        Theater theater = Theater.builder().id(2L).nombre("Y").capacidad(10).build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .fechaHora(LocalDateTime.now()).precioBase(BigDecimal.TEN)
                .occupiedSeats(10).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.isCompleto()).isTrue();
    }

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
