package com.cine.demo.movie;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.model.Movie;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void findAll_returnsAllMovies() {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").genre("Sci-Fi").durationMin(148)
                .ageRating(AgeRating.TWELVE).active(true).build();
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<MovieResponseDTO> result = movieService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
    }

    @Test
    void findActive_returnsOnlyActiveMovies() {
        Movie movie = Movie.builder()
                .id(1L).title("Active").durationMin(100).active(true).build();
        when(movieRepository.findByActiveTrue()).thenReturn(List.of(movie));

        List<MovieResponseDTO> result = movieService.findActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Active");
    }

    @Test
    void findById_returnsMovie_whenExists() {
        Movie movie = Movie.builder()
                .id(1L).title("Inception").durationMin(148).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieResponseDTO result = movieService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    void findById_throwsRuntime_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");
    }

    @Test
    void save_createsMovie_whenImageIsNull() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("New").description("desc").genre("Drama")
                .durationMin(120).ageRating(AgeRating.ALL).build();
        Movie saved = Movie.builder()
                .id(10L).title("New").durationMin(120).build();
        when(movieRepository.save(any())).thenReturn(saved);

        MovieResponseDTO result = movieService.save(dto, null);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("New");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void update_updatesAndReturnsMovie_whenExists() {
        Movie existing = Movie.builder()
                .id(1L).title("Old").durationMin(90).build();
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Renamed").description("d").genre("g")
                .durationMin(110).ageRating(AgeRating.SEVEN).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MovieResponseDTO result = movieService.update(1L, dto);

        assertThat(result.getTitle()).isEqualTo("Renamed");
        assertThat(result.getDurationMin()).isEqualTo(110);
    }

    @Test
    void update_throwsRuntime_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.update(99L,
                MovieRequestDTO.builder().title("x").durationMin(1).build()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void delete_softDeletesMovie_whenExists() {
        Movie existing = Movie.builder()
                .id(1L).title("Bye").durationMin(90).active(true).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));

        movieService.delete(1L);

        assertThat(existing.getActive()).isFalse();
        verify(movieRepository).save(existing);
    }

    @Test
    void delete_throwsRuntime_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.delete(99L))
                .isInstanceOf(RuntimeException.class);
    }
}
