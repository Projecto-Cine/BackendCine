package com.cine.demo.incident;

import com.cine.demo.controller.IncidentController;
import com.cine.demo.dto.response.IncidentResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
@Import(GlobalExceptionHandler.class)
class IncidentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private IncidentService incidentService;

    @Test
    void getAll_returns200WithIncidentList() throws Exception {
        IncidentResponseDTO dto = IncidentResponseDTO.builder().id(1L).title("Door malfunction").severity("HIGH").build();
        when(incidentService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Door malfunction"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        IncidentResponseDTO dto = IncidentResponseDTO.builder().id(1L).title("Door malfunction").build();
        when(incidentService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Door malfunction"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(incidentService.findById(99L)).thenThrow(new ResourceNotFoundException("Incident not found with id: 99"));

        mockMvc.perform(get("/api/incidents/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Incident not found with id: 99"));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        IncidentResponseDTO response = IncidentResponseDTO.builder().id(1L).title("Door malfunction").severity("HIGH").build();
        when(incidentService.save(any())).thenReturn(response);

        String body = "{\"title\":\"Door malfunction\",\"severity\":\"HIGH\",\"resolved\":false}";
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.severity").value("HIGH"));
    }

    @Test
    void create_returns400_whenTitleBlank() throws Exception {
        String body = "{\"title\":\"\",\"severity\":\"HIGH\"}";
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        IncidentResponseDTO response = IncidentResponseDTO.builder().id(1L).title("Fixed door").resolved(true).build();
        when(incidentService.update(eq(1L), any())).thenReturn(response);

        String body = "{\"title\":\"Fixed door\",\"severity\":\"LOW\",\"resolved\":true}";
        mockMvc.perform(put("/api/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resolved").value(true));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        when(incidentService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Incident not found with id: 99"));

        String body = "{\"title\":\"X\",\"severity\":\"LOW\",\"resolved\":false}";
        mockMvc.perform(put("/api/incidents/99")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Incident deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Incident not found with id: 99")).when(incidentService).delete(99L);

        mockMvc.perform(delete("/api/incidents/99"))
                .andExpect(status().isNotFound());
    }
}
