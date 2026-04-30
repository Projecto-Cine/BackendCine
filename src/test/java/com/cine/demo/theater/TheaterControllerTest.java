package com.cine.demo.theater;

import com.cine.demo.controller.TheaterController;
import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.SeatService;
import com.cine.demo.service.TheaterService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TheaterController.class)
@Import(GlobalExceptionHandler.class)
class TheaterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TheaterService theaterService;

    @MockitoBean
    private SeatService seatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returns200WithTheaterList() throws Exception {
        TheaterResponseDTO theater = TheaterResponseDTO.builder().id(1L).nombre("Sala 1").capacidad(50).build();
        when(theaterService.getAll()).thenReturn(List.of(theater));

        mockMvc.perform(get("/api/theaters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nombre").value("Sala 1"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        TheaterResponseDTO theater = TheaterResponseDTO.builder().id(1L).nombre("Sala 1").capacidad(50).build();
        when(theaterService.getById(1L)).thenReturn(theater);

        mockMvc.perform(get("/api/theaters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Sala 1"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(theaterService.getById(99L)).thenThrow(new ResourceNotFoundException("Sala no encontrada con id: 99"));

        mockMvc.perform(get("/api/theaters/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        TheaterRequestDTO request = TheaterRequestDTO.builder().nombre("Sala 2").capacidad(100).build();
        TheaterResponseDTO response = TheaterResponseDTO.builder().id(2L).nombre("Sala 2").capacidad(100).build();
        when(theaterService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Sala 2"));
    }

    @Test
    void create_returns409_whenNameAlreadyExists() throws Exception {
        TheaterRequestDTO request = TheaterRequestDTO.builder().nombre("Sala 1").capacidad(50).build();
        when(theaterService.create(any())).thenThrow(new ConflictException("Ya existe una sala con el nombre: Sala 1"));

        mockMvc.perform(post("/api/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        TheaterRequestDTO invalid = TheaterRequestDTO.builder().nombre("").capacidad(0).build();

        mockMvc.perform(post("/api/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/theaters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Sala no encontrada con id: 99"))
                .when(theaterService).delete(99L);

        mockMvc.perform(delete("/api/theaters/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getSeats_returns200WithSeatList() throws Exception {
        SeatResponseDTO seat = SeatResponseDTO.builder().id(1L).theaterId(1L).fila("A").numero(1).tipo("STANDARD").build();
        when(seatService.getByTheater(1L)).thenReturn(List.of(seat));

        mockMvc.perform(get("/api/theaters/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].fila").value("A"));
    }
}
