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

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getMovie().getTitle()).isEqualTo("Inception");
        assertThat(dto.getTheater().getName()).isEqualTo("Sala 1");
        assertThat(dto.getAvailableSeats()).isEqualTo(97);
        assertThat(dto.isFull()).isFalse();
        assertThat(dto.getBasePrice()).isEqualByComparingTo("8.5");
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

        assertThat(dto.isFull()).isTrue();
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

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getScreeningId()).isEqualTo(50L);
        assertThat(dto.getSeat().getId()).isEqualTo(7L);
        assertThat(dto.getSeat().getRow()).isEqualTo("A");
        assertThat(dto.isOccupied()).isTrue();
    }
}
