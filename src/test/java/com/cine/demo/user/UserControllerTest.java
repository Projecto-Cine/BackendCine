package com.cine.demo.user;

import com.cine.demo.controller.UserController;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.ConflictException;
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
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returns200WithUserList() throws Exception {
        UserResponseDTO user = UserResponseDTO.builder().id(1L).name("Ana").email("ana@test.com").build();
        when(userService.getAll(any())).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Ana"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        UserResponseDTO user = UserResponseDTO.builder().id(1L).name("Ana").build();
        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ana"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Ana")
                .email("ana@test.com")
                .password("secret123")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();
        UserResponseDTO response = UserResponseDTO.builder().id(1L).name("Ana").email("ana@test.com").build();
        when(userService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("ana@test.com"));
    }

    @Test
    void create_returns409_whenEmailDuplicated() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("Ana")
                .email("ana@test.com")
                .password("secret123")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();
        when(userService.create(any())).thenThrow(new ConflictException("A user already exists with email: ana@test.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user already exists with email: ana@test.com"));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        UserRequestDTO invalid = UserRequestDTO.builder()
                .name("")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found with id: 99"))
                .when(userService).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99"));
    }

    @Test
    void update_returns200_whenValid() throws Exception {
        com.cine.demo.dto.request.UpdateUserRequestDTO request =
                com.cine.demo.dto.request.UpdateUserRequestDTO.builder()
                        .name("Ana Maria").email("ana@cine.com").build();
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L).name("Ana Maria").email("ana@cine.com").build();
        when(userService.update(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Ana Maria"));
    }

    @Test
    void update_returns404_whenUserNotFound() throws Exception {
        com.cine.demo.dto.request.UpdateUserRequestDTO request =
                com.cine.demo.dto.request.UpdateUserRequestDTO.builder().name("Juan").build();
        when(userService.update(org.mockito.ArgumentMatchers.eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadImage_returns200_whenSuccessful() throws Exception {
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L).name("Ana").imageUrl("https://cdn/img.png").build();
        when(userService.uploadImage(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(response);

        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile(
                        "file", "img.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/api/users/1/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Image uploaded successfully"))
                .andExpect(jsonPath("$.data.imageUrl").value("https://cdn/img.png"));
    }
}
