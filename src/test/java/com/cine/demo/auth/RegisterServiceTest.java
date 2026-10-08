package com.cine.demo.auth;

import com.cine.demo.dto.request.RegisterRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtUtil;
import com.cine.demo.service.EmailService;
import com.cine.demo.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Alta de clientes.
 *
 * El registro estaba roto por tres motivos encadenados: el endpoint publico no
 * existia, la tienda llamaba a uno que exigia token, y ese devolvia un DTO sin
 * token cuando el frontend esperaba token y usuario. Estos tests fijan el
 * contrato para que no se pueda volver a romper en silencio.
 */
class RegisterServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private EmailService emailService;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        emailService = mock(EmailService.class);

        authService = new AuthServiceImpl(
                userRepository, employeeRepository, passwordEncoder, jwtUtil, emailService);

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash-simulado");
        when(jwtUtil.generateToken(anyLong(), anyString(), any(Role.class))).thenReturn("un.token.jwt");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(7L);
            return u;
        });
    }

    private static RegisterRequestDTO registration() {
        return RegisterRequestDTO.builder()
                .name("Ana")
                .lastName("Garcia")
                .email("ana@test.com")
                .password("secreto123")
                .birthDate(LocalDate.of(1995, 5, 10))
                .build();
    }

    private User savedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        return captor.getValue();
    }

    // --- Contrato de respuesta ------------------------------------------------

    @Test
    void register_returnsTokenAndUser_soTheSessionStartsImmediately() {
        LoginResponseDTO response = authService.register(registration());

        assertThat(response.token()).isEqualTo("un.token.jwt");
        assertThat(response.user()).isNotNull();
        assertThat(response.user().email()).isEqualTo("ana@test.com");
        assertThat(response.user().role()).isEqualTo("CLIENT");
    }

    @Test
    void register_persistsTheAccount() {
        authService.register(registration());

        assertThat(savedUser().getName()).isEqualTo("Ana");
        assertThat(savedUser().getLastName()).isEqualTo("Garcia");
    }

    // --- Lo que el cliente no decide -----------------------------------------

    @Test
    void register_alwaysAssignsTheClientRole() {
        authService.register(registration());

        assertThat(savedUser().getRole()).isEqualTo(Role.CLIENT);
    }

    @Test
    void register_neverGrantsTheFidelityDiscount() {
        // Supone un 10 por ciento de rebaja: no puede nacer activo.
        authService.register(registration());

        assertThat(savedUser().isDiscountActive()).isFalse();
        assertThat(savedUser().getAnnualVisits()).isZero();
    }

    /**
     * El tipo de usuario fija el precio de la entrada, asi que se deriva de la
     * fecha de nacimiento en lugar de aceptarse de la peticion.
     */
    @Test
    void register_derivesAdultTypeFromBirthDate() {
        authService.register(registration());

        assertThat(savedUser().getUserType()).isEqualTo(UserType.ADULT);
    }

    @Test
    void register_derivesSeniorTypeFromBirthDate() {
        authService.register(RegisterRequestDTO.builder()
                .name("Jose").lastName("Lopez").email("jose@test.com")
                .password("secreto123")
                .birthDate(LocalDate.now().minusYears(70))
                .build());

        assertThat(savedUser().getUserType()).isEqualTo(UserType.SENIOR);
    }

    @Test
    void register_neverAssignsStudentType() {
        // La tarifa de estudiante la acredita el personal, no el propio cliente.
        authService.register(registration());

        assertThat(savedUser().getUserType()).isNotEqualTo(UserType.STUDENT);
    }

    // --- Contrasena y email ---------------------------------------------------

    @Test
    void register_storesThePasswordHashed() {
        authService.register(registration());

        assertThat(savedUser().getPassword()).isEqualTo("$2a$10$hash-simulado");
        assertThat(savedUser().getPassword()).isNotEqualTo("secreto123");
        verify(passwordEncoder).encode("secreto123");
    }

    @Test
    void register_normalisesTheEmail() {
        authService.register(RegisterRequestDTO.builder()
                .name("Ana").lastName("Garcia").email("  ANA@Test.COM  ")
                .password("secreto123").birthDate(LocalDate.of(1995, 5, 10))
                .build());

        assertThat(savedUser().getEmail()).isEqualTo("ana@test.com");
    }

    @Test
    void register_rejectsAnEmailAlreadyRegistered() {
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registration()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ana@test.com");
    }

    @Test
    void register_checksForDuplicatesUsingTheNormalisedEmail() {
        when(userRepository.existsByEmail("ana@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(RegisterRequestDTO.builder()
                .name("Ana").lastName("Garcia").email("ANA@TEST.COM")
                .password("secreto123").birthDate(LocalDate.of(1995, 5, 10))
                .build()))
                .isInstanceOf(ConflictException.class);
    }

    // --- Correo de bienvenida -------------------------------------------------

    @Test
    void register_sendsTheWelcomeEmail() {
        authService.register(registration());

        verify(emailService).sendMemberWelcome("ana@test.com", "Ana");
    }

    @Test
    void register_succeedsEvenIfTheWelcomeEmailFails() {
        // La cuenta ya existe: un fallo de SMTP no puede tumbar el alta.
        doThrow(new RuntimeException("SMTP caido"))
                .when(emailService).sendMemberWelcome(anyString(), anyString());

        assertThatCode(() -> authService.register(registration())).doesNotThrowAnyException();
        verify(userRepository).save(any(User.class));
    }
}
