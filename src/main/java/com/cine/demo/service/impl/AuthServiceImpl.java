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
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!isPasswordValid(dto.getPassword(), user)) {
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
    @Transactional
    public LoginResponseDTO employeeLogin(LoginRequestDTO dto) {
        Employee employee = employeeRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), employee.getPassword())) {
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

    private boolean isPasswordValid(String rawPassword, User user) {
        String stored = user.getPassword();
        if (stored.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, stored);
        }
        // Plain text password: compare and migrate to BCrypt
        if (rawPassword.equals(stored)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
