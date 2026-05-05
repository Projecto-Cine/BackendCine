package com.cine.demo.service.impl;

import com.cine.demo.config.JwtUtil;
import com.cine.demo.dto.request.ClientRegisterRequestDTO;
import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.dto.response.UserSummaryDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.model.enums.Role;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = findUserByUsernameOrEmail(dto.getUsername());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        if ("inactive".equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is disabled");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return LoginResponseDTO.builder()
                .token(token)
                .user(userMapper.toSummaryDto(user))
                .build();
    }

    @Override
    public LoginResponseDTO register(ClientRegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }

        String username = (dto.getUsername() != null && !dto.getUsername().isBlank())
                ? dto.getUsername()
                : generateUsername(dto.getEmail());

        if (userRepository.existsByUsername(username)) {
            username = generateUsername(dto.getEmail());
        }

        User user = User.builder()
                .name(dto.getName())
                .username(username)
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .dateOfBirth(dto.getDateOfBirth())
                .student(dto.isStudent())
                .role(Role.CLIENT)
                .status("active")
                .visitsPerYear(0)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return LoginResponseDTO.builder()
                .token(token)
                .user(userMapper.toSummaryDto(user))
                .build();
    }

    @Override
    public UserSummaryDTO me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return userMapper.toSummaryDto(user);
    }

    private User findUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
    }

    private String generateUsername(String email) {
        String base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        String candidate = base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}