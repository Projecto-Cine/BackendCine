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

    @Test
    void getAll_returns200WithApiResponseAndSeatList() throws Exception {
        SeatResponseDTO seat = SeatResponseDTO.builder()
                .id(1L).row("A").number(1).type("STANDARD").build();
        when(seatService.getAll()).thenReturn(List.of(seat));

        mockMvc.perform(get("/api/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seats retrieved successfully"))
                .andExpect(jsonPath("$.data[0].row").value("A"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        when(seatService.getById(1L)).thenReturn(
                SeatResponseDTO.builder().id(1L).row("B").number(2).build());

        mockMvc.perform(get("/api/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.row").value("B"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(seatService.getById(99L)).thenThrow(new ResourceNotFoundException("Seat not found with id: 99"));

        mockMvc.perform(get("/api/seats/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat not found with id: 99"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        SeatRequestDTO request = SeatRequestDTO.builder()
                .theaterId(1L).row("A").number(1).type("STANDARD").build();
        SeatResponseDTO response = SeatResponseDTO.builder()
                .id(10L).row("A").number(1).type("STANDARD").build();
        when(seatService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seat created successfully"));
    }

    @Test
    void create_returns409_whenDuplicate() throws Exception {
        SeatRequestDTO request = SeatRequestDTO.builder()
                .theaterId(1L).row("A").number(1).type("STANDARD").build();
        when(seatService.create(any())).thenThrow(new ConflictException("Seat A1 already exists in this theater"));

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat A1 already exists in this theater"));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        SeatRequestDTO invalid = SeatRequestDTO.builder()
                .theaterId(null).row("").number(0).type(null).build();

        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.theaterId").isNotEmpty());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        UpdateSeatRequestDTO request = UpdateSeatRequestDTO.builder().type("VIP").build();
        SeatResponseDTO response = SeatResponseDTO.builder().id(1L).type("VIP").build();
        when(seatService.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/seats/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("VIP"));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/seats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Seat deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Seat not found with id: 99"))
                .when(seatService).delete(99L);

        mockMvc.perform(delete("/api/seats/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat not found with id: 99"));
    }
}
