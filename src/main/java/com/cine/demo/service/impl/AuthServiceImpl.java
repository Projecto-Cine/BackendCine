package com.cine.demo.service.impl;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.exception.UnauthorizedException;
import com.cine.demo.model.User;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtService;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!isPasswordValid(dto.password(), user)) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        return LoginResponseDTO.builder()
                .token(token)
                .user(LoginResponseDTO.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .imageUrl(user.getImageUrl())
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
