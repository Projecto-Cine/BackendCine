package com.cine.demo.auth;

import com.cine.demo.controller.AuthController;
import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.exception.GlobalExceptionHandler;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

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
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token("a.b.c")
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(1L).name("Ana").email("ana@cine.com")
                        .role(com.cine.demo.model.enums.Role.CLIENT.name())
                        .status("ACTIVE").build())
                .build();
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("a.b.c"))
                .andExpect(jsonPath("$.user.email").value("ana@cine.com"))
                .andExpect(jsonPath("$.user.role").value("CLIENT"));
    }

    @Test
    void login_returns401_whenCredentialsInvalid() throws Exception {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("ana@cine.com").password("wrong").build();
        when(authService.login(any())).thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void login_returns400_whenValidationFails() throws Exception {
        LoginRequestDTO invalid = LoginRequestDTO.builder()
                .email("not-an-email").password("").build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }
}
