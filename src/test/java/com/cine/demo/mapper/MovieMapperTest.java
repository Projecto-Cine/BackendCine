package com.cine.demo.mapper;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.request.UpdateMovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import com.cine.demo.model.enums.AgeRating;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MovieMapperTest {

    private final MovieMapper mapper = new MovieMapper();

    @Test
    void toEntity_copiesAllFieldsAndDefaultsActiveTrue() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .titulo("Inception").descripcion("Sueño dentro de sueño")
                .genero("Sci-Fi").duracionMin(148).clasificacionEdad(AgeRating.TWELVE)
                .build();

        Movie entity = mapper.toEntity(dto);

        assertThat(entity.getTitulo()).isEqualTo("Inception");
        assertThat(entity.getDescripcion()).isEqualTo("Sueño dentro de sueño");
        assertThat(entity.getGenero()).isEqualTo("Sci-Fi");
        assertThat(entity.getDuracionMin()).isEqualTo(148);
        assertThat(entity.getClasificacionEdad()).isEqualTo(AgeRating.TWELVE);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void toResponseDto_copiesAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Movie movie = Movie.builder()
                .id(7L).titulo("Matrix").descripcion("Realidad simulada")
                .genero("Acción").duracionMin(120).clasificacionEdad(AgeRating.SIXTEEN)
                .posterUrl("http://img/matrix.jpg").active(true).createdAt(now).build();

        MovieResponseDTO dto = mapper.toResponseDto(movie);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getTitulo()).isEqualTo("Matrix");
        assertThat(dto.getDescripcion()).isEqualTo("Realidad simulada");
        assertThat(dto.getGenero()).isEqualTo("Acción");
        assertThat(dto.getDuracionMin()).isEqualTo(120);
        assertThat(dto.getClasificacionEdad()).isEqualTo("SIXTEEN");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Movie existing = Movie.builder()
                .titulo("Antiguo").descripcion("desc original").genero("Drama")
                .duracionMin(100).clasificacionEdad(AgeRating.ALL).build();
        UpdateMovieRequestDTO patch = UpdateMovieRequestDTO.builder().titulo("Nuevo título").build();

        mapper.updateEntityFromDto(patch, existing);

        assertThat(existing.getTitulo()).isEqualTo("Nuevo título");
        assertThat(existing.getDescripcion()).isEqualTo("desc original");
        assertThat(existing.getGenero()).isEqualTo("Drama");
        assertThat(existing.getDuracionMin()).isEqualTo(100);
        assertThat(existing.getClasificacionEdad()).isEqualTo(AgeRating.ALL);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Movie existing = Movie.builder()
                .titulo("X").descripcion("Y").genero("Z").duracionMin(1)
                .clasificacionEdad(AgeRating.ALL).posterUrl("a").build();
        UpdateMovieRequestDTO dto = UpdateMovieRequestDTO.builder()
                .titulo("T").descripcion("D").genero("G").duracionMin(99)
                .clasificacionEdad(AgeRating.EIGHTEEN).build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getTitulo()).isEqualTo("T");
        assertThat(existing.getDescripcion()).isEqualTo("D");
        assertThat(existing.getGenero()).isEqualTo("G");
        assertThat(existing.getDuracionMin()).isEqualTo(99);
        assertThat(existing.getClasificacionEdad()).isEqualTo(AgeRating.EIGHTEEN);
    }
}
