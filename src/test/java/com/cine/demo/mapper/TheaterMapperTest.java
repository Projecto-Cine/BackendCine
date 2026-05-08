package com.cine.demo.mapper;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.model.Theater;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TheaterMapperTest {

    private final TheaterMapper mapper = new TheaterMapper();

    /**
     * Verifica que TheaterRequestDTO se convierte en una entidad Theater
     * con nombre y capacidad correctos. La entidad NO debe traer id porque
     * ese lo asigna la base de datos en el insert.
     */
    @Test
    void toEntity_mapsNameAndCapacity() {
        TheaterRequestDTO dto = TheaterRequestDTO.builder()
                .nombre("Sala IMAX").capacidad(120).build();

        Theater entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNombre()).isEqualTo("Sala IMAX");
        assertThat(entity.getCapacidad()).isEqualTo(120);
    }

    /**
     * Verifica que toResponseDto incluye id, nombre, capacidad y además
     * totalSeats que en este modelo coincide con la capacidad.
     */
    @Test
    void toResponseDto_includesIdAndTotalSeatsEqualsCapacity() {
        Theater theater = Theater.builder()
                .id(5L).nombre("Sala 5").capacidad(80).build();

        TheaterResponseDTO dto = mapper.toResponseDto(theater);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getNombre()).isEqualTo("Sala 5");
        assertThat(dto.getCapacidad()).isEqualTo(80);
        assertThat(dto.getTotalSeats()).isEqualTo(80);
    }

    /**
     * Comprobamos el "patch": si pasamos un DTO sólo con nombre,
     * la capacidad anterior debe mantenerse.
     */
    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Theater existing = Theater.builder().nombre("Vieja").capacidad(50).build();
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder().nombre("Renombrada").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("Renombrada");
        assertThat(existing.getCapacidad()).isEqualTo(50); // sin cambios
    }

    /**
     * Si el DTO trae nombre Y capacidad, ambos deben actualizarse.
     */
    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Theater existing = Theater.builder().nombre("a").capacidad(1).build();
        UpdateTheaterRequestDTO dto = UpdateTheaterRequestDTO.builder()
                .nombre("b").capacidad(99).build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getNombre()).isEqualTo("b");
        assertThat(existing.getCapacidad()).isEqualTo(99);
    }

    /**
     * Si el DTO viene completamente vacío, la entidad NO sufre cambios.
     */
    @Test
    void updateEntityFromDto_keepsEntity_whenAllNull() {
        Theater existing = Theater.builder().nombre("Estable").capacidad(42).build();
        UpdateTheaterRequestDTO empty = UpdateTheaterRequestDTO.builder().build();

        mapper.updateEntityFromDto(empty, existing);

        assertThat(existing.getNombre()).isEqualTo("Estable");
        assertThat(existing.getCapacidad()).isEqualTo(42);
    }
}
