package com.cine.demo.config;

import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.model.enums.UserType;
import com.cine.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        ensureUser("admin@lumen.com",   "lumen2024", "Admin",   "Lumen", Role.ADMIN);
        ensureUser("cliente@lumen.com", "lumen2024", "Cliente", "Lumen", Role.CLIENTE);
    }

    private void ensureUser(String email, String password, String nombre, String lastName, Role rol) {
        userRepository.findByEmail(email).ifPresentOrElse(
            user -> {
                if (!user.getPassword().startsWith("$2")) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                }
            },
            () -> {
                User user = User.builder()
                        .nombre(nombre)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .userType(UserType.ADULT)
                        .rol(rol)
                        .build();
                userRepository.save(user);
            }
        );
    }
}
