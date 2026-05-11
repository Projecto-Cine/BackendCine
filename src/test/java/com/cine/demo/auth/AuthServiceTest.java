package com.cine.demo.auth;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtService;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(10L).name("Ana").email("ana@cine.com")
                .password("$2a$10$ENCODED_PASSWORD_LIKE_STRING")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.CLIENTE).build();
    }

    @Test
    void login_returnsTokenAndUserData_whenCredentialsValid() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("ana@cine.com");
        dto.setPassword("plain-password");
        when(userRepository.findByEmail("ana@cine.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plain-password", "$2a$10$ENCODED_PASSWORD_LIKE_STRING")).thenReturn(true);
        when(jwtService.generateToken("ana@cine.com")).thenReturn("issued.jwt.token");

        LoginResponseDTO result = authService.login(dto);

        assertThat(result.getToken()).isEqualTo("issued.jwt.token");
        assertThat(result.getUser().getEmail()).isEqualTo("ana@cine.com");
        assertThat(result.getUser().getName()).isEqualTo("Ana");
        assertThat(result.getUser().getRole()).isEqualTo(Role.CLIENTE);
    }

    @Test
    void login_throwsUnauthorizedException_whenEmailDoesNotExist() {
        when(userRepository.findByEmail("missing@cine.com")).thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("missing@cine.com");
        dto.setPassword("x");
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void login_throwsUnauthorizedException_whenPasswordWrong() {
        when(userRepository.findByEmail("ana@cine.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "$2a$10$ENCODED_PASSWORD_LIKE_STRING")).thenReturn(false);

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("ana@cine.com");
        dto.setPassword("wrong");
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
