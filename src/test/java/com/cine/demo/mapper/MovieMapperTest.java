package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import com.cine.demo.model.enums.AgeRating;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MovieMapperTest {

    private final MovieMapper mapper = new MovieMapper();

    /**
     * Comprueba la conversión DTO → entidad Movie.
     * Verificamos que TODOS los campos del DTO llegan a la entidad y que
     * por defecto active = true (el negocio asume que las películas creadas
     * empiezan activas).
     */
    @Test
    void toEntity_copiesAllFieldsAndDefaultsActiveTrue() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Inception").description("Sueño dentro de sueño")
                .genre("Sci-Fi").durationMin(148).ageRating(AgeRating.TWELVE)
                .imageUrl("http://img/inception.jpg").build();

        Movie entity = mapper.toEntity(dto);

        assertThat(entity.getTitle()).isEqualTo("Inception");
        assertThat(entity.getDescription()).isEqualTo("Sueño dentro de sueño");
        assertThat(entity.getGenre()).isEqualTo("Sci-Fi");
        assertThat(entity.getDurationMin()).isEqualTo(148);
        assertThat(entity.getAgeRating()).isEqualTo(AgeRating.TWELVE);
        assertThat(entity.getImageUrl()).isEqualTo("http://img/inception.jpg");
        assertThat(entity.getActive()).isTrue();
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    /**
     * Comprueba la conversión inversa entidad → DTO.
     * Esto cubre el camino que usa el controlador al devolver una respuesta JSON.
     */
    @Test
    void toResponseDto_copiesAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Movie movie = Movie.builder()
                .id(7L).title("Matrix").description("Realidad simulada")
                .genre("Acción").durationMin(120).ageRating(AgeRating.SIXTEEN)
                .imageUrl("http://img/matrix.jpg").active(true).createdAt(now).build();

        MovieResponseDTO dto = mapper.toResponseDto(movie);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getTitle()).isEqualTo("Matrix");
        assertThat(dto.getDescription()).isEqualTo("Realidad simulada");
        assertThat(dto.getGenre()).isEqualTo("Acción");
        assertThat(dto.getDurationMin()).isEqualTo(120);
        assertThat(dto.getAgeRating()).isEqualTo(AgeRating.SIXTEEN);
        assertThat(dto.getActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    /**
     * updateEntityFromDto solo debe sobrescribir los campos NO nulos.
     * Aquí pasamos un DTO con sólo el título y verificamos que el resto
     * de campos (género, duración, etc.) NO se pisan.
     */
    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Movie existing = Movie.builder()
                .title("Antiguo").description("desc original").genre("Drama")
                .durationMin(100).ageRating(AgeRating.ALL).build();
        MovieRequestDTO patch = MovieRequestDTO.builder().title("Nuevo título").build();

        mapper.updateEntityFromDto(patch, existing);

        assertThat(existing.getTitle()).isEqualTo("Nuevo título");
        // Los siguientes campos no estaban en el DTO → no deben cambiar
        assertThat(existing.getDescription()).isEqualTo("desc original");
        assertThat(existing.getGenre()).isEqualTo("Drama");
        assertThat(existing.getDurationMin()).isEqualTo(100);
        assertThat(existing.getAgeRating()).isEqualTo(AgeRating.ALL);
    }

    /**
     * Si pasamos un DTO con TODOS los campos, todos deben actualizarse.
     */
    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Movie existing = Movie.builder()
                .title("X").description("Y").genre("Z").durationMin(1)
                .ageRating(AgeRating.ALL).imageUrl("a").build();
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("T").description("D").genre("G").durationMin(99)
                .ageRating(AgeRating.EIGHTEEN).imageUrl("b").build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getTitle()).isEqualTo("T");
        assertThat(existing.getDescription()).isEqualTo("D");
        assertThat(existing.getGenre()).isEqualTo("G");
        assertThat(existing.getDurationMin()).isEqualTo(99);
        assertThat(existing.getAgeRating()).isEqualTo(AgeRating.EIGHTEEN);
        assertThat(existing.getImageUrl()).isEqualTo("b");
    }
}
