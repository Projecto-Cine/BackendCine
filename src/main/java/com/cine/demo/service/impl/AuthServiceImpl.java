package com.cine.demo.service.impl;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.AuthResponseDTO;
import com.cine.demo.exception.ConflictException;
import com.cine.demo.mapper.UserMapper;
import com.cine.demo.model.User;
import com.cine.demo.repository.UserRepository;
import com.cine.demo.security.JwtUtil;
import com.cine.demo.security.UnauthorizedException;
import com.cine.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDTO register(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRol());
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInSeconds(jwtUtil.getExpirationMillis() / 1000L)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRol().name())
                .build();
    }
}
