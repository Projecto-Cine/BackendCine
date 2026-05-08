package com.cine.demo.mapper;

import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.model.Seat;
import com.cine.demo.model.Theater;
import com.cine.demo.model.enums.SeatType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeatMapperTest {

    private final SeatMapper mapper = new SeatMapper();

    /**
     * Conversión Seat → DTO. Es importante que el id de la sala (theaterId)
     * se extraiga del objeto Theater anidado, y que el tipo de asiento
     * se serialice como cadena (no como enum) para el JSON de salida.
     */
    @Test
    void toResponseDto_extractsTheaterIdAndSerializesTypeAsString() {
        Theater theater = Theater.builder().id(3L).nombre("Sala A").build();
        Seat seat = Seat.builder()
                .id(11L).theater(theater).fila("F").numero(8).tipo(SeatType.VIP).build();

        SeatResponseDTO dto = mapper.toResponseDto(seat);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getTheaterId()).isEqualTo(3L);
        assertThat(dto.getFila()).isEqualTo("F");
        assertThat(dto.getNumero()).isEqualTo(8);
        assertThat(dto.getTipo()).isEqualTo("VIP");
    }

    /**
     * Patch parcial: si el DTO sólo cambia el tipo, fila y número se conservan.
     */
    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Seat seat = Seat.builder().fila("A").numero(1).tipo(SeatType.STANDARD).build();
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder().tipo("VIP").build();

        mapper.updateEntityFromDto(dto, seat);

        assertThat(seat.getTipo()).isEqualTo(SeatType.VIP);
        assertThat(seat.getFila()).isEqualTo("A");
        assertThat(seat.getNumero()).isEqualTo(1);
    }

    /**
     * Si el DTO trae los tres campos, todos deben aplicarse al asiento.
     */
    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Seat seat = Seat.builder().fila("A").numero(1).tipo(SeatType.STANDARD).build();
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder()
                .fila("Z").numero(99).tipo("VIP").build();

        mapper.updateEntityFromDto(dto, seat);

        assertThat(seat.getFila()).isEqualTo("Z");
        assertThat(seat.getNumero()).isEqualTo(99);
        assertThat(seat.getTipo()).isEqualTo(SeatType.VIP);
    }

    /**
     * Si el DTO viene vacío, nada cambia. Confirma que las comprobaciones
     * "if (... != null)" funcionan correctamente.
     */
    @Test
    void updateEntityFromDto_keepsSeat_whenAllNull() {
        Seat seat = Seat.builder().fila("B").numero(5).tipo(SeatType.VIP).build();

        mapper.updateEntityFromDto(UpdateSeatRequestDTO.builder().build(), seat);

        assertThat(seat.getFila()).isEqualTo("B");
        assertThat(seat.getNumero()).isEqualTo(5);
        assertThat(seat.getTipo()).isEqualTo(SeatType.VIP);
    }
}
