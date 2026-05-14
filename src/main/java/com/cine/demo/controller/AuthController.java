package com.cine.demo.controller;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login with email and password, returns JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Returns a JWT token for use in other endpoints")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/employee-login")
    @Operation(summary = "Employee login", description = "Authenticates an employee and returns a JWT with their role (GERENCIA, CAJERO, LIMPIEZA or MANTENIMIENTO)")
    public ResponseEntity<LoginResponseDTO> employeeLogin(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.employeeLogin(dto));
    }
}
