package com.cine.demo.movie;

import com.cine.demo.controller.MovieController;
import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import(GlobalExceptionHandler.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returns200WithMovieList() throws Exception {
        MovieResponseDTO movie = MovieResponseDTO.builder().id(1L).title("Inception").genre("Sci-Fi").build();
        when(movieService.findAll()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        MovieResponseDTO movie = MovieResponseDTO.builder().id(1L).title("Inception").build();
        when(movieService.findById(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getById_returns500_whenNotFound() throws Exception {
        when(movieService.findById(99L)).thenThrow(new RuntimeException("Movie not found"));

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void create_returns200_whenValid() throws Exception {
        MovieRequestDTO request = MovieRequestDTO.builder()
                .title("Inception").durationMin(148).genre("Sci-Fi").ageRating(AgeRating.TWELVE).build();
        MovieResponseDTO response = MovieResponseDTO.builder().id(1L).title("Inception").genre("Sci-Fi").build();
        when(movieService.save(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void delete_returns204_whenExists() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns500_whenNotFound() throws Exception {
        doThrow(new RuntimeException("Movie not found"))
                .when(movieService).delete(99L);

        mockMvc.perform(delete("/api/movies/99"))
                .andExpect(status().isInternalServerError());
    }
}