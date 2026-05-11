package com.cine.demo.movie;

import com.cine.demo.controller.MovieController;
import com.cine.demo.dto.request.MovieRequestDTO;
import com.cine.demo.dto.response.MovieResponseDTO;
import com.cine.demo.exception.ConflictException;
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
        MovieResponseDTO movie = MovieResponseDTO.builder()
                .id(1L).titulo("Inception").genero("Sci-Fi").duracionMin(148).build();
        when(movieService.findAll()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titulo").value("Inception"))
                .andExpect(jsonPath("$.data[0].genero").value("Sci-Fi"));
    }

    @Test
    void getActive_returns200WithActiveMovies() throws Exception {
        MovieResponseDTO movie = MovieResponseDTO.builder()
                .id(1L).titulo("Active Film").active(true).build();
        when(movieService.findActive()).thenReturn(List.of(movie));

        mockMvc.perform(get("/api/movies/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titulo").value("Active Film"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        MovieResponseDTO movie = MovieResponseDTO.builder().id(1L).titulo("Inception").build();
        when(movieService.findById(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titulo").value("Inception"));
    }

    @Test
    void create_returns201_whenValidJson() throws Exception {
        MovieRequestDTO request = MovieRequestDTO.builder()
                .titulo("Inception").duracionMin(148).genero("Sci-Fi").clasificacionEdad(AgeRating.TWELVE).build();
        MovieResponseDTO response = MovieResponseDTO.builder().id(1L).titulo("Inception").genero("Sci-Fi").build();
        when(movieService.save(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.titulo").value("Inception"));
    }

    @Test
    void create_returns409_whenTitleAlreadyExists() throws Exception {
        MovieRequestDTO request = MovieRequestDTO.builder()
                .titulo("Inception").duracionMin(148).genero("Sci-Fi").clasificacionEdad(AgeRating.TWELVE).build();
        when(movieService.save(any(), any())).thenThrow(new ConflictException("Ya existe una película con el título: Inception"));

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        MovieRequestDTO invalid = MovieRequestDTO.builder().titulo("").duracionMin(0).genero("").clasificacionEdad(null).build();

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        MovieRequestDTO request = MovieRequestDTO.builder()
                .titulo("Updated").duracionMin(120).genero("Drama").clasificacionEdad(AgeRating.ALL).build();
        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(1L).titulo("Updated").genero("Drama").build();
        when(movieService.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titulo").value("Updated"));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void delete_propagatesNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Película no encontrada con id: 99"))
                .when(movieService).delete(99L);

        mockMvc.perform(delete("/api/movies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createWithImage_returns200_whenValidMultipart() throws Exception {
        MovieResponseDTO response = MovieResponseDTO.builder()
                .id(1L).titulo("Inception").posterUrl("/uploads/movies/img.png").build();
        when(movieService.save(any(), any())).thenReturn(response);

        org.springframework.mock.web.MockMultipartFile movieJson =
                new org.springframework.mock.web.MockMultipartFile(
                        "movie", "movie", MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(MovieRequestDTO.builder()
                                .titulo("Inception").duracionMin(148).build()));
        org.springframework.mock.web.MockMultipartFile image =
                new org.springframework.mock.web.MockMultipartFile(
                        "image", "img.png", "image/png", new byte[]{1, 2});

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/api/movies")
                        .file(movieJson).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titulo").value("Inception"))
                .andExpect(jsonPath("$.data.posterUrl").value("/uploads/movies/img.png"));
    }
}
