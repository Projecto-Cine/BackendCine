package com.cine.demo.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScreeningSeatTest {

    @Test
    void isEffectivelyTaken_returnsFalse_whenNotOccupiedAndNoReservation() {
        ScreeningSeat seat = ScreeningSeat.builder().occupied(false).reservedUntil(null).build();
        assertThat(seat.isEffectivelyTaken()).isFalse();
    }

    @Test
    void isEffectivelyTaken_returnsTrue_whenOccupied() {
        ScreeningSeat seat = ScreeningSeat.builder().occupied(true).reservedUntil(null).build();
        assertThat(seat.isEffectivelyTaken()).isTrue();
    }

    @Test
    void isEffectivelyTaken_returnsTrue_whenReservedUntilInFuture() {
        ScreeningSeat seat = ScreeningSeat.builder()
                .occupied(false)
                .reservedUntil(LocalDateTime.now().plusMinutes(5))
                .build();
        assertThat(seat.isEffectivelyTaken()).isTrue();
    }

    @Test
    void isEffectivelyTaken_returnsFalse_whenReservedUntilInPast() {
        ScreeningSeat seat = ScreeningSeat.builder()
                .occupied(false)
                .reservedUntil(LocalDateTime.now().minusMinutes(1))
                .build();
        assertThat(seat.isEffectivelyTaken()).isFalse();
    }
}