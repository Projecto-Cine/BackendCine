package com.cine.demo.movie;

import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.mapper.MovieMapper;
import com.cine.demo.model.enums.AgeRating;
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
    void save_throwsRuntimeException_whenTitleAlreadyExists() {
        MovieRequestDTO dto = MovieRequestDTO.builder()
                .title("Inception").durationMin(148).genre("Sci-Fi").ageRating(AgeRating.TWELVE).build();
        when(movieRepository.existsByTitle("Inception")).thenReturn(true);
        when(movieRepository.existsByTitle("Inception")).thenReturn(true);

        assertThatThrownBy(() -> movieService.save(dto, null));
    }

    @Test
    void findById_throwsRuntimeException_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void delete_throwsRuntimeException_whenNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
}