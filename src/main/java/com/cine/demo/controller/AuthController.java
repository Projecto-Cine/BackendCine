package com.cine.demo.controller;

import com.cine.demo.dto.request.ClientRegisterRequestDTO;
import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.dto.response.UserSummaryDTO;
import com.cine.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody ClientRegisterRequestDTO dto) {
        return ResponseEntity.status(201).body(authService.register(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, UserSummaryDTO>> me() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(Map.of("user", authService.me(userId)));
    }
}