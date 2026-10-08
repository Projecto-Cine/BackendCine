package com.cine.demo.service.impl;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.model.Employee;
import com.cine.demo.model.User;
import com.cine.demo.repository.EmployeeRepository;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtUtil;
import com.cine.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!isPasswordValid(dto.password(), user)) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return LoginResponseDTO.builder()
                .token(token)
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .imageUrl(user.getImageUrl())
                        .status("ACTIVE")
                        .build())
                .build();
    }

    @Override
    public LoginResponseDTO employeeLogin(LoginRequestDTO dto) {
        Employee employee = employeeRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), employee.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String roleDisplayName = employee.getRole().getDisplayName();
        String token = jwtUtil.generateToken(employee.getId(), employee.getEmail(), roleDisplayName);

        return LoginResponseDTO.builder()
                .token(token)
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .email(employee.getEmail())
                        .role(roleDisplayName)
                        .status("ACTIVE")
                        .build())
                .build();
    }

    /**
     * Solo se aceptan contrasenas hasheadas con bcrypt.
     *
     * Antes habia un camino alternativo que comparaba la contrasena en claro
     * contra el valor almacenado y la migraba al vuelo. Eso implicaba aceptar
     * credenciales en claro en base de datos como via de login valida. La
     * migracion de datos sembrados ya la cubre DataInitializer, que rehashea
     * cualquier valor que no sea bcrypt al arrancar.
     */
    private boolean isPasswordValid(String rawPassword, User user) {
        String stored = user.getPassword();
        if (stored == null || stored.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, stored);
    }
}
