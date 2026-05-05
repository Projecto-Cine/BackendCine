package com.cine.demo.seat;

import com.cine.demo.controller.SeatController;
import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.SeatService;
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

@WebMvcTest(SeatController.class)
@Import(GlobalExceptionHandler.class)
class SeatControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private SeatService seatService;

    /**
     * GET /api/seats devuelve TODOS los asientos envueltos en ApiResponse
     * con success = true y mensaje en español.
     */
    @Test
    void getAll_returns200WithApiResponseAndSeatList() throws Exception {
        SeatResponseDTO seat = SeatResponseDTO.builder()
                .id(1L).fila("A").numero(1).tipo("STANDARD").build();
        when(seatService.getAll()).thenReturn(List.of(seat));

        mockMvc.perform(get("/api/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Asientos obtenidos correctamente"))
                .andExpect(jsonPath("$.data[0].fila").value("A"));
    }

    /**
     * GET /api/seats/{id}: caso feliz, encontrado.
     */
    @Test
    void getById_returns200_whenExists() throws Exception {
        when(seatService.getById(1L)).thenReturn(
                SeatResponseDTO.builder().id(1L).fila("B").numero(2).build());

        mockMvc.perform(get("/api/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fila").value("B"));
    }

    /**
     * GET /api/seats/{id}: si no existe, el GlobalExceptionHandler
     * captura la excepción y devuelve 404 con success = false.
     */
    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(seatService.getById(99L)).thenThrow(new ResourceNotFoundException("Asiento no encontrado con id: 99"));

        mockMvc.perform(get("/api/seats/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * POST /api/seats: caso feliz devuelve 201 Created.
     */
    @Test
    void create_returns201_whenValid() throws Exception {
        SeatRequestDTO request = SeatRequestDTO.builder()
                .theaterId(1L).fila("A").numero(1).tipo("STANDARD").build();
        SeatResponseDTO response = SeatResponseDTO.builder()
                .id(10L).fila("A").numero(1).tipo("STANDARD").build();
        when(seatService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Asiento creado correctamente"));
    }

    /**
     * POST /api/seats con asiento duplicado en la misma sala: 409 Conflict.
     */
    @Test
    void create_returns409_whenDuplicate() throws Exception {
        SeatRequestDTO request = SeatRequestDTO.builder()
                .theaterId(1L).fila("A").numero(1).tipo("STANDARD").build();
        when(seatService.create(any())).thenThrow(new ConflictException("Ya existe el asiento A1 en esa sala"));

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * POST /api/seats con datos inválidos (sin theaterId, fila vacía):
     * 400 Bad Request por @Valid.
     */
    @Test
    void create_returns400_whenValidationFails() throws Exception {
        SeatRequestDTO invalid = SeatRequestDTO.builder()
                .theaterId(null).fila("").numero(0).tipo(null).build();

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error de validación"));
    }

    /**
     * PUT /api/seats/{id}: caso feliz devuelve 200 con el asiento modificado.
     */
    @Test
    void update_returns200_whenValid() throws Exception {
        UpdateSeatRequestDTO request = UpdateSeatRequestDTO.builder().tipo("VIP").build();
        SeatResponseDTO response = SeatResponseDTO.builder().id(1L).tipo("VIP").build();
        when(seatService.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/seats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tipo").value("VIP"));
    }

    /**
     * DELETE /api/seats/{id}: caso feliz devuelve 200 con mensaje de éxito.
     */
    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Asiento eliminado correctamente"));
    }

    /**
     * DELETE /api/seats/{id}: 404 si el asiento no existe.
     */
    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Asiento no encontrado con id: 99"))
                .when(seatService).delete(99L);

        mockMvc.perform(delete("/api/seats/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
