package com.cine.demo.screening;

import com.cine.demo.controller.ScreeningController;
import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.exception.SeatAlreadyTakenException;
import com.cine.demo.exception.ScreeningFullException;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScreeningController.class)
@Import(GlobalExceptionHandler.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScreeningService screeningService;

    @MockitoBean
    private PurchaseService purchaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returns200WithScreeningList() throws Exception {
        ScreeningResponseDTO screening = ScreeningResponseDTO.builder()
                .id(1L).availableSeats(50).basePrice(BigDecimal.TEN).build();
        when(screeningService.getAll()).thenReturn(List.of(screening));

        mockMvc.perform(get("/api/screenings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].availableSeats").value(50));
    }

    @Test
    void getUpcoming_returns200WithUpcomingList() throws Exception {
        ScreeningResponseDTO screening = ScreeningResponseDTO.builder().id(1L).availableSeats(10).build();
        when(screeningService.getUpcoming()).thenReturn(List.of(screening));

        mockMvc.perform(get("/api/screenings/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        ScreeningResponseDTO screening = ScreeningResponseDTO.builder().id(1L).availableSeats(20).build();
        when(screeningService.getById(1L)).thenReturn(screening);

        mockMvc.perform(get("/api/screenings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(screeningService.getById(99L)).thenThrow(new ResourceNotFoundException("Proyección no encontrada con id: 99"));

        mockMvc.perform(get("/api/screenings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getByMovie_returns200WithList() throws Exception {
        ScreeningResponseDTO screening = ScreeningResponseDTO.builder().id(1L).build();
        when(screeningService.getByMovie(1L)).thenReturn(List.of(screening));

        mockMvc.perform(get("/api/screenings/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        ScreeningRequestDTO request = ScreeningRequestDTO.builder()
                .movieId(1L).theaterId(1L)
                .dateTime(LocalDateTime.now().plusDays(7))
                .basePrice(BigDecimal.valueOf(12.50))
                .build();
        ScreeningResponseDTO response = ScreeningResponseDTO.builder()
                .id(1L).availableSeats(50).basePrice(BigDecimal.valueOf(12.50)).build();
        when(screeningService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.availableSeats").value(50));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        ScreeningRequestDTO invalid = ScreeningRequestDTO.builder().build();

        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/screenings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Proyección no encontrada con id: 99"))
                .when(screeningService).delete(99L);

        mockMvc.perform(delete("/api/screenings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void reserveSeat_returns200_whenSuccessful() throws Exception {
        ScreeningSeatResponseDTO response = ScreeningSeatResponseDTO.builder()
                .id(1L).screeningId(1L).occupied(true).build();
        when(screeningService.reserveSeat(1L, 1L)).thenReturn(response);

        mockMvc.perform(post("/api/screenings/1/seats/1/reserve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.occupied").value(true));
    }

    @Test
    void reserveSeat_returns409_whenSeatAlreadyTaken() throws Exception {
        when(screeningService.reserveSeat(1L, 1L)).thenThrow(new SeatAlreadyTakenException("El asiento ya está ocupado"));

        mockMvc.perform(post("/api/screenings/1/seats/1/reserve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void reserveSeat_returns409_whenScreeningFull() throws Exception {
        when(screeningService.reserveSeat(1L, 2L)).thenThrow(new ScreeningFullException("La proyección está completa"));

        mockMvc.perform(post("/api/screenings/1/seats/2/reserve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void releaseSeat_returns200_whenSuccessful() throws Exception {
        ScreeningSeatResponseDTO response = ScreeningSeatResponseDTO.builder()
                .id(1L).screeningId(1L).occupied(false).build();
        when(screeningService.releaseSeat(1L, 1L)).thenReturn(response);

        mockMvc.perform(post("/api/screenings/1/seats/1/release"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.occupied").value(false));
    }
}