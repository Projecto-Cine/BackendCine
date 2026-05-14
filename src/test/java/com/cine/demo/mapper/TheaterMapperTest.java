package com.cine.demo.mapper;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.model.Theater;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TheaterMapperTest {

    private final TheaterMapper mapper = new TheaterMapper();

    @Test
    void toEntity_mapsNameAndCapacity() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder()
                .name("Sala IMAX").capacity(120).build();

        Theater entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Sala IMAX");
        assertThat(entity.getCapacity()).isEqualTo(120);
    }

    @Test
    void toResponseDto_includesIdAndTotalSeatsEqualsCapacity() {
        Theater theater = Theater.builder()
                .id(5L).name("Sala 5").capacity(80).build();

        TheaterResponseDTO dto = mapper.toResponseDto(theater);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Sala 5");
        assertThat(dto.getCapacity()).isEqualTo(80);
        assertThat(dto.getTotalSeats()).isEqualTo(0);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Theater existing = Theater.builder().name("Vieja").capacity(50).build();
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder().name("Renombrada").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("Renombrada");
        assertThat(existing.getCapacity()).isEqualTo(50); // no changes
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Theater existing = Theater.builder().name("a").capacity(1).build();
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder()
                .name("b").capacity(99).build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("b");
        assertThat(existing.getCapacity()).isEqualTo(99);
    }

    @Test
    void updateEntityFromDto_keepsEntity_whenAllNull() {
        Theater existing = Theater.builder().name("Estable").capacity(42).build();
        UpdateTheaterRequestDTO empty = UpdateTheaterRequestDTO.builder().build();

        mapper.updateEntityFromDto(empty, existing);

        assertThat(existing.getName()).isEqualTo("Estable");
        assertThat(existing.getCapacity()).isEqualTo(42);
    }
}
