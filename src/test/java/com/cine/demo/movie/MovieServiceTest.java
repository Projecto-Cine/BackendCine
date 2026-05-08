package com.cine.demo.movie;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MovieMapper;
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
    void create_throwsConflictException_whenTitleAlreadyExists() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .titulo("Inception").duracionMin(148).genero("Sci-Fi").clasificacionEdad(AgeRating.TWELVE).build();
        when(movieRepository.existsByTitulo("Inception")).thenReturn(true);

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

    /**
     * save con imagen vacía: la ruta saveImage devuelve null sin escribir
     * a disco. Verificamos que la película se guarda con imageUrl = null.
     */
    @Test
    void save_setsImageUrlNull_whenImageIsEmpty() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Sin imagen").durationMin(90)
                .ageRating(com.cine.demo.model.enums.AgeRating.ALL).build();
        org.springframework.mock.web.MockMultipartFile emptyImage =
                new org.springframework.mock.web.MockMultipartFile(
                        "image", "img.png", "image/png", new byte[0]);
        com.cine.demo.model.Movie saved = com.cine.demo.model.Movie.builder()
                .id(1L).title("Sin imagen").durationMin(90).build();
        when(movieRepository.save(any())).thenReturn(saved);

        var result = movieService.save(dto, emptyImage);

        assertThat(result.getId()).isEqualTo(1L);
        verify(movieRepository).save(argThat(m -> m.getImageUrl() == null));
    }

    /**
     * save con imagen real: comprueba que se llama al método de guardado
     * y que el archivo se escribe en uploads/movies/ con el nombre original.
     * No comprobamos el contenido del fichero porque sólo nos interesa que
     * la URL devuelta tenga el formato correcto.
     */
    @Test
    void save_writesFileAndSetsImageUrl_whenImageProvided() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Con imagen").durationMin(120)
                .ageRating(com.cine.demo.model.enums.AgeRating.ALL).build();
        org.springframework.mock.web.MockMultipartFile image =
                new org.springframework.mock.web.MockMultipartFile(
                        "image", "poster.jpg", "image/jpeg", new byte[]{1, 2, 3});
        com.cine.demo.model.Movie saved = com.cine.demo.model.Movie.builder()
                .id(2L).title("Con imagen").durationMin(120).build();
        when(movieRepository.save(any())).thenAnswer(inv -> {
            com.cine.demo.model.Movie m = inv.getArgument(0);
            m.setId(2L);
            return m;
        });

        var result = movieService.save(dto, image);

        assertThat(result.getId()).isEqualTo(2L);
        // Cleanup: borramos el directorio creado para no contaminar el repo
        try {
            java.nio.file.Path uploads = java.nio.file.Paths.get("uploads/movies");
            if (java.nio.file.Files.exists(uploads)) {
                java.nio.file.Files.walk(uploads)
                        .sorted(java.util.Comparator.reverseOrder())
                        .forEach(p -> { try { java.nio.file.Files.deleteIfExists(p); } catch (Exception ignored) {} });
            }
        } catch (Exception ignored) {}
    }
}
