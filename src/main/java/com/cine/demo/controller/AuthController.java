package com.cine.demo.controller;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.AuthResponseDTO;
import com.cine.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<AuthResponseDTO>builder()
                .success(true)
                .message("Inicio de sesión correcto")
                .data(authService.login(dto))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AuthResponseDTO>builder()
                        .success(true)
                        .message("Usuario registrado correctamente")
                        .data(authService.register(dto))
                        .build());
    }
}
