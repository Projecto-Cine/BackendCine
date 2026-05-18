package com.cine.demo.clients;

import com.cine.demo.controller.ClientsController;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.UserService;
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

@WebMvcTest(ClientsController.class)
@Import(GlobalExceptionHandler.class)
class ClientsControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService userService;

    @Test
    void getAll_returns200WithClientList() throws Exception {
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").email("ana@test.com").build();
        when(userService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Clients retrieved successfully"))
                .andExpect(jsonPath("$.data[0].name").value("Ana"));
    }

    @Test
    void search_returns200WithResults() throws Exception {
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").email("ana@test.com").build();
        when(userService.search("Ana")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clients/search").param("q", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("ana@test.com"));
    }

    @Test
    void search_returns200WithEmptyList_whenNoMatch() throws Exception {
        when(userService.search("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/clients/search").param("q", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        UserResponseDTO dto = UserResponseDTO.builder().id(1L).name("Ana").build();
        when(userService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Client retrieved successfully"))
                .andExpect(jsonPath("$.data.name").value("Ana"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        UserResponseDTO response = UserResponseDTO.builder().id(1L).name("Ana Updated").build();
        when(userService.update(eq(1L), any())).thenReturn(response);

        String body = "{\"name\":\"Ana Updated\"}";
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Client updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Ana Updated"));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        when(userService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        String body = "{\"name\":\"Unknown\"}";
        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Client deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99")).when(userService).delete(99L);

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }
}
