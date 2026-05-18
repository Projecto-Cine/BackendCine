package com.cine.demo.shift;

import com.cine.demo.controller.ShiftController;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.ShiftService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftController.class)
@Import(GlobalExceptionHandler.class)
class ShiftControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ShiftService shiftService;

    private ShiftResponseDTO sampleShift() {
        return ShiftResponseDTO.builder()
                .id(1L).employeeId(1L).employeeName("Carlos")
                .shiftDate(LocalDate.of(2026, 5, 13))
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(17, 0))
                .status("SCHEDULED").build();
    }

    @Test
    void getAll_returns200WithShiftList() throws Exception {
        when(shiftService.findAll()).thenReturn(List.of(sampleShift()));

        mockMvc.perform(get("/api/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].employeeName").value("Carlos"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        when(shiftService.findById(1L)).thenReturn(sampleShift());

        mockMvc.perform(get("/api/shifts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(shiftService.findById(99L)).thenThrow(new ResourceNotFoundException("Shift not found with id: 99"));

        mockMvc.perform(get("/api/shifts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Shift not found with id: 99"));
    }

    @Test
    void getByDate_returns200() throws Exception {
        when(shiftService.findByDate(LocalDate.of(2026, 5, 13))).thenReturn(List.of(sampleShift()));

        mockMvc.perform(get("/api/shifts/date/2026-05-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].employeeName").value("Carlos"));
    }

    @Test
    void getByDateRange_returns200() throws Exception {
        when(shiftService.findByDateRange(any(), any())).thenReturn(List.of(sampleShift()));

        mockMvc.perform(get("/api/shifts/range").param("from", "2026-05-01").param("to", "2026-05-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        when(shiftService.save(any())).thenReturn(sampleShift());

        String body = "{\"employeeId\":1,\"shiftDate\":\"2026-05-13\",\"startTime\":\"09:00\",\"endTime\":\"17:00\"}";
        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Shift created successfully"));
    }

    @Test
    void create_returns400_whenRequiredFieldMissing() throws Exception {
        String body = "{\"notes\":\"Evening\"}";
        mockMvc.perform(post("/api/shifts")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        ShiftResponseDTO updated = ShiftResponseDTO.builder().id(1L).status("COMPLETED").build();
        when(shiftService.update(eq(1L), any())).thenReturn(updated);

        String body = "{\"status\":\"COMPLETED\"}";
        mockMvc.perform(put("/api/shifts/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        when(shiftService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Shift not found with id: 99"));

        String body = "{\"status\":\"COMPLETED\"}";
        mockMvc.perform(put("/api/shifts/99")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/shifts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Shift deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Shift not found with id: 99")).when(shiftService).delete(99L);

        mockMvc.perform(delete("/api/shifts/99"))
                .andExpect(status().isNotFound());
    }
}
