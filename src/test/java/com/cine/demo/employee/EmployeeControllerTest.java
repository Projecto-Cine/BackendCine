package com.cine.demo.employee;

import com.cine.demo.controller.EmployeeController;
import com.cine.demo.dto.response.EmployeeResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.ResourceNotFoundException;
import com.cine.demo.service.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private EmployeeService employeeService;

    @Test
    void getAll_returns200WithEmployeeList() throws Exception {
        EmployeeResponseDTO dto = EmployeeResponseDTO.builder().id(1L).name("Carlos").email("carlos@cine.com").build();
        when(employeeService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Carlos"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        EmployeeResponseDTO dto = EmployeeResponseDTO.builder().id(1L).name("Carlos").build();
        when(employeeService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Carlos"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(employeeService.findById(99L)).thenThrow(new ResourceNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        EmployeeResponseDTO response = EmployeeResponseDTO.builder().id(1L).name("Carlos").email("carlos@cine.com").build();
        when(employeeService.save(any())).thenReturn(response);

        String body = "{\"name\":\"Carlos\",\"email\":\"carlos@cine.com\",\"role\":\"CAJERO\"}";
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("carlos@cine.com"));
    }

    @Test
    void create_returns409_whenEmailDuplicated() throws Exception {
        when(employeeService.save(any())).thenThrow(new ConflictException("An employee already exists with email: carlos@cine.com"));

        String body = "{\"name\":\"Carlos\",\"email\":\"carlos@cine.com\",\"role\":\"CAJERO\"}";
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("An employee already exists with email: carlos@cine.com"));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        String body = "{\"name\":\"\",\"email\":\"not-an-email\"}";
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        EmployeeResponseDTO response = EmployeeResponseDTO.builder().id(1L).name("Carlos Updated").build();
        when(employeeService.update(eq(1L), any())).thenReturn(response);

        String body = "{\"name\":\"Carlos Updated\"}";
        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Carlos Updated"));
    }

    @Test
    void update_returns404_whenNotFound() throws Exception {
        when(employeeService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Employee not found with id: 99"));

        String body = "{\"name\":\"X\"}";
        mockMvc.perform(put("/api/employees/99")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Employee not found with id: 99")).when(employeeService).delete(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns409_whenEmployeeHasShifts() throws Exception {
        doThrow(new ConflictException("Cannot delete employee because they have assigned shifts"))
                .when(employeeService).delete(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete employee because they have assigned shifts"));
    }
}
