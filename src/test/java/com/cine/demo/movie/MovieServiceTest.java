package com.cine.demo.movie;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MovieMapper;
import com.cine.demo.model.Movie;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.service.CloudinaryService;
import com.cine.demo.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock private MovieRepository movieRepository;
    @Mock private MovieMapper movieMapper;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void create_throwsConflictException_whenTitleAlreadyExists() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .titulo("Inception").duracionMin(148).genero("Sci-Fi").clasificacionEdad("PG-13").build();
        when(movieRepository.existsByTitulo("Inception")).thenReturn(true);

        assertThatThrownBy(() -> movieService.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Inception");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(movieRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> movieService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
