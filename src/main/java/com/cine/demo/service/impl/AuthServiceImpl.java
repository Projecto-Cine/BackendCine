package com.cine.demo.service.impl;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = findUserByUsernameOrEmail(dto.getUsername());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = generateToken(user.getId());
        return LoginResponseDTO.builder()
                .user(userMapper.toResponseDto(user))
                .token(token)
                .build();
    }

    @Override
    public UserResponseDTO me(String token) {
        Long userId = extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Token inválido"));
        return userMapper.toResponseDto(user);
    }

    private User findUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));
    }

    private String generateToken(Long userId) {
        String raw = userId + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private Long extractUserIdFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            return Long.parseLong(parts[0]);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }
    }
}