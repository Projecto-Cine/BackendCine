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
    void toResponseDto_marksFullFalse_whenSeatsAvailable() {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").durationMin(148).ageRating(AgeRating.TWELVE).build();
        Theater theater = Theater.builder().id(2L).name("Sala 1").capacity(100).build();
        Screening screening = Screening.builder()
                .id(10L).movie(movie).theater(theater)
                .startTime(LocalDateTime.of(2030, 1, 1, 20, 0))
                .basePrice(BigDecimal.valueOf(8.5))
                .occupiedSeats(3).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.movie().title()).isEqualTo("Inception");
        assertThat(dto.theater().name()).isEqualTo("Sala 1");
        assertThat(dto.availableSeats()).isEqualTo(97);
        assertThat(dto.full()).isFalse();
        assertThat(dto.basePrice()).isEqualByComparingTo("8.5");
    }

    @Test
    void toResponseDto_marksFullTrue_whenZeroSeatsAvailable() {
        Movie movie = Movie.builder().id(1L).title("X").durationMin(90).build();
        Theater theater = Theater.builder().id(2L).name("Y").capacity(10).build();
        Screening screening = Screening.builder()
                .id(1L).movie(movie).theater(theater)
                .startTime(LocalDateTime.now()).basePrice(BigDecimal.TEN)
                .occupiedSeats(10).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.full()).isTrue();
        assertThat(dto.availableSeats()).isEqualTo(0);
    }

    @Test
    void toResponseDto_mapsStartTimeAndBasePrice() {
        Movie movie = Movie.builder().id(1L).title("Dune").durationMin(155).build();
        Theater theater = Theater.builder().id(1L).name("Sala IMAX").capacity(200).build();
        LocalDateTime start = LocalDateTime.of(2030, 7, 4, 18, 0);
        Screening screening = Screening.builder()
                .id(5L).movie(movie).theater(theater)
                .startTime(start).basePrice(BigDecimal.valueOf(12))
                .occupiedSeats(50).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.startTime()).isEqualTo(start);
        assertThat(dto.basePrice()).isEqualByComparingTo("12");
        assertThat(dto.availableSeats()).isEqualTo(150);
    }

    @Test
    void toResponseDto_mapsMovieAndTheaterDetails() {
        Movie movie = Movie.builder().id(3L).title("Oppenheimer").durationMin(180).ageRating(AgeRating.SIXTEEN).build();
        Theater theater = Theater.builder().id(4L).name("Sala 3").capacity(80).build();
        Screening screening = Screening.builder()
                .id(20L).movie(movie).theater(theater)
                .startTime(LocalDateTime.now().plusDays(2))
                .basePrice(BigDecimal.valueOf(9))
                .occupiedSeats(0).build();

        ScreeningResponseDTO dto = mapper.toResponseDto(screening);

        assertThat(dto.movie().id()).isEqualTo(3L);
        assertThat(dto.movie().title()).isEqualTo("Oppenheimer");
        assertThat(dto.theater().id()).isEqualTo(4L);
        assertThat(dto.theater().name()).isEqualTo("Sala 3");
    }

    @Test
    void toScreeningSeatResponseDto_includesScreeningIdAndSeatInfo() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").build();
        Seat seat = Seat.builder().id(7L).theater(theater)
                .row("A").number(5).type(SeatType.STANDARD).build();
        Screening screening = Screening.builder().id(50L).theater(theater)
                .startTime(LocalDateTime.now()).basePrice(BigDecimal.ONE).build();
        ScreeningSeat ss = ScreeningSeat.builder()
                .id(99L).screening(screening).seat(seat).occupied(true).build();

        ScreeningSeatResponseDTO dto = mapper.toScreeningSeatResponseDto(ss);

        assertThat(dto.id()).isEqualTo(99L);
        assertThat(dto.screeningId()).isEqualTo(50L);
        assertThat(dto.seat().id()).isEqualTo(7L);
        assertThat(dto.seat().row()).isEqualTo("A");
        assertThat(dto.occupied()).isTrue();
    }

    @Test
    void toScreeningSeatResponseDto_marksOccupiedFalse_whenFree() {
        Theater theater = Theater.builder().id(1L).name("Sala 1").build();
        Seat seat = Seat.builder().id(3L).theater(theater)
                .row("B").number(2).type(SeatType.VIP).build();
        Screening screening = Screening.builder().id(10L).theater(theater)
                .startTime(LocalDateTime.now()).basePrice(BigDecimal.TEN).build();
        ScreeningSeat ss = ScreeningSeat.builder()
                .id(55L).screening(screening).seat(seat).occupied(false).build();

        ScreeningSeatResponseDTO dto = mapper.toScreeningSeatResponseDto(ss);

        assertThat(dto.occupied()).isFalse();
        assertThat(dto.seat().row()).isEqualTo("B");
    }
}
