package com.cine.demo.controller;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true)
                .message("Usuarios obtenidos correctamente")
                .data(userService.getAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Usuario obtenido correctamente")
                .data(userService.getById(id))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("Usuario creado correctamente")
                        .data(userService.create(dto))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Usuario actualizado correctamente")
                .data(userService.update(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Usuario eliminado correctamente")
                .build());
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<UserResponseDTO>> uploadImage(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Imagen subida correctamente")
                .data(userService.uploadImage(id, file))
                .build());
    }
}
