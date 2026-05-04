package com.cine.demo.auth;

import com.cine.demo.controller.AuthController;
import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.AuthResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.security.UnauthorizedException;
import com.cine.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_returns200AndToken_whenCredentialsValid() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("ana@cine.com").password("secret123").build();
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token("a.b.c").tokenType("Bearer").expiresInSeconds(1800L)
                .userId(1L).email("ana@cine.com").role("CLIENTE").build();
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inicio de sesión correcto"))
                .andExpect(jsonPath("$.data.token").value("a.b.c"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(1800));
    }

    @Test
    void login_returns401_whenCredentialsInvalid() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("ana@cine.com").password("wrong").build();
        when(authService.login(any())).thenThrow(new UnauthorizedException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void login_returns400_whenValidationFails() throws Exception {
        LoginRequestDTO invalid = LoginRequestDTO.builder()
                .email("not-an-email").password("").build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error de validación"));
    }

    @Test
    void register_returns201AndToken_whenValid() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .nombre("Nueva").email("nueva@cine.com").password("secret123")
                .fechaNacimiento(LocalDate.of(2000, 1, 1)).build();
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token("x.y.z").tokenType("Bearer").expiresInSeconds(900L)
                .userId(99L).email("nueva@cine.com").role("CLIENTE").build();
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.data.token").value("x.y.z"))
                .andExpect(jsonPath("$.data.userId").value(99));
    }

    @Test
    void register_returns409_whenEmailAlreadyExists() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .nombre("Ana").email("ana@cine.com").password("secret123")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).build();
        when(authService.register(any()))
                .thenThrow(new ConflictException("Ya existe un usuario con el email: ana@cine.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_returns400_whenValidationFails() throws Exception {
        UserRequestDTO invalid = UserRequestDTO.builder()
                .nombre("").email("not-an-email").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
