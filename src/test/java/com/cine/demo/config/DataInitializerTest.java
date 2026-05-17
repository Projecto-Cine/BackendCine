package com.cine.demo.config;

import com.cine.demo.model.Employee;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock private UserRepository userRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedHash");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(movieRepository.existsByTitle(anyString())).thenReturn(false);
    }

    // ── Users ─────────────────────────────────────────────────────────────

    @Test
    void run_createsAllDefaultUsers_whenNoneExist() throws Exception {
        dataInitializer.run();

        // 6 users: admin, cliente, supervisor, operator, ticket, mantenimiento
        verify(userRepository, times(6)).save(any(User.class));
    }

    @Test
    void run_skipsUserCreation_whenUserAlreadyExistsWithBCryptPassword() throws Exception {
        User existing = User.builder()
                .email("admin@lumen.com").password("$2a$10$alreadyEncoded")
                .name("Admin").role(Role.ADMIN).birthDate(LocalDate.of(1990, 1, 1)).build();
        when(userRepository.findByEmail("admin@lumen.com")).thenReturn(Optional.of(existing));

        dataInitializer.run();

        // admin user is skipped, the other 5 are saved
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atMost(5)).save(captor.capture());
        captor.getAllValues().forEach(u ->
                assertThat(u.getEmail()).isNotEqualTo("admin@lumen.com"));
    }

    @Test
    void run_reEncodesPassword_whenUserExistsWithPlainPassword() throws Exception {
        User existing = User.builder()
                .email("admin@lumen.com").password("plaintext")
                .name("Admin").role(Role.ADMIN).birthDate(LocalDate.of(1990, 1, 1)).build();
        when(userRepository.findByEmail("admin@lumen.com")).thenReturn(Optional.of(existing));

        dataInitializer.run();

        verify(passwordEncoder).encode("lumen2024");
        verify(userRepository).save(existing);
        assertThat(existing.getPassword()).isEqualTo("$2a$10$encodedHash");
    }

    @Test
    void run_savesUserWithCorrectRole() throws Exception {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        dataInitializer.run();

        verify(userRepository, times(6)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(User::getRole)
                .contains(Role.ADMIN, Role.CLIENTE, Role.SUPERVISOR, Role.OPERATOR, Role.TICKET, Role.MAINTENANCE);
    }

    // ── Employees ─────────────────────────────────────────────────────────

    @Test
    void run_createsAllDefaultEmployees_whenNoneExist() throws Exception {
        dataInitializer.run();

        // 4 employees: Carlos, María, José, Ana
        verify(employeeRepository, times(4)).save(any(Employee.class));
    }

    @Test
    void run_skipsEmployeeCreation_whenAlreadyExists() throws Exception {
        Employee existing = Employee.builder()
                .name("Carlos").email("carlos@lumen.com").role(EmployeeRole.CAJERO).build();
        when(employeeRepository.findByEmail("carlos@lumen.com")).thenReturn(Optional.of(existing));

        dataInitializer.run();

        // only 3 employees saved (Carlos skipped)
        verify(employeeRepository, times(3)).save(any(Employee.class));
    }

    @Test
    void run_savesEmployeeWithCorrectRole() throws Exception {
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);

        dataInitializer.run();

        verify(employeeRepository, times(4)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(Employee::getRole)
                .contains(EmployeeRole.CAJERO, EmployeeRole.GERENCIA, EmployeeRole.SEGURIDAD, EmployeeRole.LIMPIEZA);
    }

    // ── Movies ────────────────────────────────────────────────────────────

    @Test
    void run_createsAllDefaultMovies_whenNoneExist() throws Exception {
        dataInitializer.run();

        // 5 movies
        verify(movieRepository, times(5)).save(any());
    }

    @Test
    void run_skipsMovieCreation_whenAlreadyExists() throws Exception {
        when(movieRepository.existsByTitle("Dune: Part Two")).thenReturn(true);

        dataInitializer.run();

        verify(movieRepository, times(4)).save(any());
    }

    @Test
    void run_createsNoMovies_whenAllAlreadyExist() throws Exception {
        when(movieRepository.existsByTitle(anyString())).thenReturn(true);

        dataInitializer.run();

        verify(movieRepository, never()).save(any());
    }
}
