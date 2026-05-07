package com.cine.demo.auth;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.AuthResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtUtil;
import com.cine.demo.security.UnauthorizedException;
import com.cine.demo.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(10L).name("Ana").email("ana@cine.com")
                .password("ENCODED_PASSWORD")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.CLIENTE).build();
    }

    @Test
    void login_returnsTokenAndUserData_whenCredentialsValid() {
        LoginRequestDTO dto = LoginRequestDTO.builder()
                .email("ana@cine.com").password("plain-password").build();
        when(userRepository.findByEmail("ana@cine.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plain-password", "ENCODED_PASSWORD")).thenReturn(true);
        when(jwtUtil.generateToken(10L, "ana@cine.com", Role.CLIENTE)).thenReturn("issued.jwt.token");
        when(jwtUtil.getExpirationMillis()).thenReturn(30L * 60_000L);

        AuthResponseDTO result = authService.login(dto);

        assertThat(result.getToken()).isEqualTo("issued.jwt.token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresInSeconds()).isEqualTo(1800L);
        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getEmail()).isEqualTo("ana@cine.com");
        assertThat(result.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    void login_throwsUnauthorizedException_whenEmailDoesNotExist() {
        when(userRepository.findByEmail("missing@cine.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                LoginRequestDTO.builder().email("missing@cine.com").password("x").build()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void login_throwsUnauthorizedException_whenPasswordWrong() {
        when(userRepository.findByEmail("ana@cine.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "ENCODED_PASSWORD")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                LoginRequestDTO.builder().email("ana@cine.com").password("wrong").build()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void register_throwsConflictException_whenEmailAlreadyExists() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Ana").email("ana@cine.com").password("p")
                .birthDate(LocalDate.of(1990, 1, 1)).build();
        when(userRepository.existsByEmail("ana@cine.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ana@cine.com");
    }

    @Test
    void register_encodesPasswordAndPersistsUser_whenEmailNew() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Nueva").email("nueva@cine.com").password("plain")
                .birthDate(LocalDate.of(2000, 5, 12)).build();
        User entityFromMapper = User.builder()
                .name("Nueva").email("nueva@cine.com").password("plain")
                .birthDate(LocalDate.of(2000, 5, 12))
                .role(Role.CLIENTE).build();
        User saved = User.builder()
                .id(99L).name("Nueva").email("nueva@cine.com")
                .password("BCRYPT").birthDate(LocalDate.of(2000, 5, 12))
                .role(Role.CLIENTE).build();

        when(userRepository.existsByEmail("nueva@cine.com")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(entityFromMapper);
        when(passwordEncoder.encode("plain")).thenReturn("BCRYPT");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtUtil.generateToken(99L, "nueva@cine.com", Role.CLIENTE)).thenReturn("new.jwt.token");
        when(jwtUtil.getExpirationMillis()).thenReturn(15L * 60_000L);

        AuthResponseDTO result = authService.register(dto);

        verify(passwordEncoder).encode("plain");
        verify(userRepository).save(argThat(u -> "BCRYPT".equals(u.getPassword())));
        assertThat(result.getToken()).isEqualTo("new.jwt.token");
        assertThat(result.getUserId()).isEqualTo(99L);
        assertThat(result.getExpiresInSeconds()).isEqualTo(900L);
    }
}