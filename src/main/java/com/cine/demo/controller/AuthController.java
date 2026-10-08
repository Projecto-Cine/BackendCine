package com.cine.demo.controller;

import com.cine.demo.dto.request.LoginRequestDTO;
import com.cine.demo.dto.request.RegisterRequestDTO;
import com.cine.demo.dto.response.LoginResponseDTO;
import com.cine.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /**
     * Alta de cliente. Devuelve token y usuario, igual que el login, para que
     * el alta deje la sesion iniciada y el cliente siga con su compra.
     *
     * Este endpoint no existia. El filtro de seguridad declaraba publica la
     * ruta /api/auth/register, pero no habia nada detras: la tienda llamaba a
     * /api/users/quick-register, que exigia token y devolvia un DTO sin token,
     * de modo que el registro era imposible por dos motivos distintos a la vez.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a client", description = "Creates a client account and returns a JWT")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/employee-login")
    @Operation(summary = "Employee login", description = "Authenticates an employee and returns a JWT with their role (GERENCIA, CAJERO, LIMPIEZA or MANTENIMIENTO)")
    public ResponseEntity<LoginResponseDTO> employeeLogin(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.employeeLogin(dto));
    }
}
