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
        UserResponseDTO user = UserResponseDTO.builder().id(1L).nombre("Ana").email("ana@test.com").build();
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nombre").value("Ana"));
    }

    @Test
    void getById_returns200_whenExists() throws Exception {
        UserResponseDTO user = UserResponseDTO.builder().id(1L).nombre("Ana").build();
        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Ana"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns201_whenValid() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .nombre("Ana")
                .email("ana@test.com")
                .password("secret123")
                .fechaNacimiento(LocalDate.of(1995, 1, 1))
                .build();
        UserResponseDTO response = UserResponseDTO.builder().id(1L).nombre("Ana").email("ana@test.com").build();
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
                .nombre("Ana")
                .email("ana@test.com")
                .password("secret123")
                .fechaNacimiento(LocalDate.of(1995, 1, 1))
                .build();
        when(userService.create(any())).thenThrow(new ConflictException("Ya existe un usuario con el email: ana@test.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        UserRequestDTO invalid = UserRequestDTO.builder()
                .nombre("")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void delete_returns200_whenExists() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Usuario no encontrado con id: 99"))
                .when(userService).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * PUT /api/users/{id}: caso feliz devuelve 200 con el usuario actualizado.
     */
    @Test
    void update_returns200_whenValid() throws Exception {
        com.cine.demo.dto.request.UpdateUserRequestDTO request =
                com.cine.demo.dto.request.UpdateUserRequestDTO.builder()
                        .nombre("Ana María").email("ana@cine.com").build();
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L).nombre("Ana María").email("ana@cine.com").build();
        when(userService.update(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Ana María"));
    }

    /**
     * PUT /api/users/{id}: 404 si no existe.
     */
    @Test
    void update_returns404_whenUserNotFound() throws Exception {
        com.cine.demo.dto.request.UpdateUserRequestDTO request =
                com.cine.demo.dto.request.UpdateUserRequestDTO.builder().nombre("Juan").build();
        when(userService.update(org.mockito.ArgumentMatchers.eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con id: 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /api/users/{id}/image: subida correcta de imagen.
     * Comprueba que el endpoint multipart funciona y delega en
     * userService.uploadImage(...).
     */
    @Test
    void uploadImage_returns200_whenSuccessful() throws Exception {
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L).nombre("Ana").imagenUrl("https://cdn/img.png").build();
        when(userService.uploadImage(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(response);

        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile(
                        "file", "img.png", "image/png", new byte[]{1, 2, 3});

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/api/users/1/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Imagen subida correctamente"))
                .andExpect(jsonPath("$.data.imagenUrl").value("https://cdn/img.png"));
    }
}
