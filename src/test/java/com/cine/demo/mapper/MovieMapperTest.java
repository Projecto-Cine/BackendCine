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
                .title("Inception").description("Sueño dentro de sueño")
                .genre("Sci-Fi").durationMin(148).ageRating(AgeRating.TWELVE)
                .build();

        Movie entity = mapper.toEntity(dto);

        assertThat(entity.getTitle()).isEqualTo("Inception");
        assertThat(entity.getDescription()).isEqualTo("Sueño dentro de sueño");
        assertThat(entity.getGenre()).isEqualTo("Sci-Fi");
        assertThat(entity.getDurationMin()).isEqualTo(148);
        assertThat(entity.getAgeRating()).isEqualTo(AgeRating.TWELVE);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void toResponseDto_copiesAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Movie movie = Movie.builder()
                .id(7L).title("Matrix").description("Realidad simulada")
                .genre("Acción").durationMin(120).ageRating(AgeRating.SIXTEEN)
                .posterUrl("http://img/matrix.jpg").active(true).createdAt(now).build();

        MovieResponseDTO dto = mapper.toResponseDto(movie);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getTitle()).isEqualTo("Matrix");
        assertThat(dto.getDescription()).isEqualTo("Realidad simulada");
        assertThat(dto.getGenre()).isEqualTo("Acción");
        assertThat(dto.getDurationMin()).isEqualTo(120);
        assertThat(dto.getAgeRating()).isEqualTo("SIXTEEN");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void updateEntityFromDto_onlyOverwritesNonNullFields() {
        Movie existing = Movie.builder()
                .title("Antiguo").description("desc original").genre("Drama")
                .durationMin(100).ageRating(AgeRating.ALL).build();
        UpdateMovieRequestDTO patch = UpdateMovieRequestDTO.builder().title("Nuevo título").build();

        mapper.updateEntityFromDto(patch, existing);

        assertThat(existing.getTitle()).isEqualTo("Nuevo título");
        assertThat(existing.getDescription()).isEqualTo("desc original");
        assertThat(existing.getGenre()).isEqualTo("Drama");
        assertThat(existing.getDurationMin()).isEqualTo(100);
        assertThat(existing.getAgeRating()).isEqualTo(AgeRating.ALL);
    }

    @Test
    void updateEntityFromDto_overwritesAllFields_whenAllProvided() {
        Movie existing = Movie.builder()
                .title("X").description("Y").genre("Z").durationMin(1)
                .ageRating(AgeRating.ALL).posterUrl("a").build();
        UpdateMovieRequestDTO dto = UpdateMovieRequestDTO.builder()
                .title("T").description("D").genre("G").durationMin(99)
                .ageRating(AgeRating.EIGHTEEN).build();

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getTitle()).isEqualTo("T");
        assertThat(existing.getDescription()).isEqualTo("D");
        assertThat(existing.getGenre()).isEqualTo("G");
        assertThat(existing.getDurationMin()).isEqualTo(99);
        assertThat(existing.getAgeRating()).isEqualTo(AgeRating.EIGHTEEN);
    }
}
