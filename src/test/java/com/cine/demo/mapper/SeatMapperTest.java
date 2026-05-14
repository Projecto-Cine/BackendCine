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

    @Test
    void toResponseDto_extractsTheaterIdAndSerializesTypeAsString() {
        Theater theater = Theater.builder().id(3L).name("Sala A").build();
        Seat seat = Seat.builder()
                .id(11L).theater(theater).row("F").number(8).type(SeatType.VIP).build();

        SeatResponseDTO dto = mapper.toResponseDto(seat);

        assertThat(dto.getId()).isEqualTo(11L);
        assertThat(dto.getTheaterId()).isEqualTo(3L);
        assertThat(dto.getRow()).isEqualTo("F");
        assertThat(dto.getNumber()).isEqualTo(8);
        assertThat(dto.getType()).isEqualTo("VIP");
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Seat seat = Seat.builder().row("A").number(1).type(SeatType.STANDARD).build();
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder().type("VIP").build();

        mapper.updateEntityFromDto(dto, seat);

        assertThat(seat.getType()).isEqualTo(SeatType.VIP);
        assertThat(seat.getRow()).isEqualTo("A");
        assertThat(seat.getNumber()).isEqualTo(1);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Seat seat = Seat.builder().row("A").number(1).type(SeatType.STANDARD).build();
        UpdateSeatRequestDTO dto = UpdateSeatRequestDTO.builder()
                .row("Z").number(99).type("VIP").build();

        mapper.updateEntityFromDto(dto, seat);

        assertThat(seat.getRow()).isEqualTo("Z");
        assertThat(seat.getNumber()).isEqualTo(99);
        assertThat(seat.getType()).isEqualTo(SeatType.VIP);
    }

    @Test
    void updateEntityFromDto_keepsSeat_whenAllNull() {
        Seat seat = Seat.builder().row("B").number(5).type(SeatType.VIP).build();

        mapper.updateEntityFromDto(UpdateSeatRequestDTO.builder().build(), seat);

        assertThat(seat.getRow()).isEqualTo("B");
        assertThat(seat.getNumber()).isEqualTo(5);
        assertThat(seat.getType()).isEqualTo(SeatType.VIP);
    }
}