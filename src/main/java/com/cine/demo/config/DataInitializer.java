package com.cine.demo.config;

import com.cine.demo.model.Employee;
import com.cine.demo.model.Movie;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.AgeRating;
import com.cine.demo.model.enums.EmployeeRole;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.MovieRepository;
import com.cine.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        jdbcTemplate.update("UPDATE workers SET role = 'MANTENIMIENTO' WHERE UPPER(role) = 'SEGURIDAD'");
        String encoded = passwordEncoder.encode("lumen2024");
        jdbcTemplate.update("UPDATE workers SET password = ? WHERE password IS NULL OR password = ''", encoded);

        ensureUser("admin@lumen.com",        "lumen2024", "Admin",        "Lumen", Role.ADMIN);
        ensureUser("cliente@lumen.com",      "lumen2024", "Client",       "Lumen", Role.CLIENT);
        ensureUser("supervisor@lumen.com",   "lumen2024", "Supervisor",   "Lumen", Role.SUPERVISOR);
        ensureUser("operator@lumen.com",     "lumen2024", "Operator",     "Lumen", Role.OPERATOR);
        ensureUser("ticket@lumen.com",       "lumen2024", "Ticketer",     "Lumen", Role.TICKET);
        ensureUser("maintenance@lumen.com",  "lumen2024", "Maintenance",  "Lumen", Role.MAINTENANCE);

        ensureEmployee("Carlos", "cajero@lumen.es",        "lumen2024", EmployeeRole.CASHIER);
        ensureEmployee("Maria",  "gerencia@lumen.es",      "lumen2024", EmployeeRole.MANAGEMENT);
        ensureEmployee("Jose",   "mantenimiento@lumen.es", "lumen2024", EmployeeRole.MAINTENANCE);
        ensureEmployee("Ana",    "limpieza@lumen.es",      "lumen2024", EmployeeRole.CLEANING);

        ensureMovie("Dune: Part Two",       "La lucha por Arrakis continúa.",        166, "Ciencia ficción", AgeRating.TWELVE,  "ES", "2D");
        ensureMovie("Godzilla x Kong",      "Dos titanes unen fuerzas.",             115, "Acción",          AgeRating.SIXTEEN, "VO", "3D");
        ensureMovie("Kung Fu Panda 4",      "Po busca un nuevo Guerrero Dragón.",     94, "Animación",       AgeRating.ALL,      "ES", "2D");
        ensureMovie("Civil War",            "Periodistas en una América distópica.", 109, "Drama",           AgeRating.EIGHTEEN, "VOSE", "2D");
        ensureMovie("The First Omen",       "El origen del mal.",                    119, "Terror",          AgeRating.EIGHTEEN, "VO", "2D");
    }

    private void ensureMovie(String title, String description, int durationMin, String genre, AgeRating ageRating, String language, String schedule) {
        if (!movieRepository.existsByTitle(title)) {
            Movie movie = Movie.builder()
                    .title(title)
                    .description(description)
                    .durationMin(durationMin)
                    .genre(genre)
                    .ageRating(ageRating)
                    .language(language)
                    .schedule(schedule)
                    .active(true)
                    .build();
            movieRepository.save(movie);
        }
    }

    private void ensureUser(String email, String password, String name, String lastName, Role role) {
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                if (!user.getPassword().startsWith("$2")) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                }
            },
            () -> {
                User user = User.builder()
                        .name(name)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .birthDate(LocalDate.of(1990, 1, 1))
                        .userType(UserType.ADULT)
                        .role(role)
                        .build();
                userRepository.save(user);
            }
        );
    }

    private void ensureEmployee(String name, String email, String password, EmployeeRole role) {
        employeeRepository.findByEmail(email).ifPresentOrElse(
            emp -> {
                String stored = emp.getPassword();
                if (stored == null || stored.isEmpty() || !passwordEncoder.matches(password, stored)) {
                    emp.setPassword(passwordEncoder.encode(password));
                    employeeRepository.save(emp);
                }
            },
            () -> {
                Employee employee = Employee.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(role)
                        .build();
                employeeRepository.save(employee);
            }
        );
    }
}