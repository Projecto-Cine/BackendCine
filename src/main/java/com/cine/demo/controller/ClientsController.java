package com.cine.demo.controller;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientsController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true)
                .message("Clients retrieved successfully")
                .data(userService.getAll())
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true)
                .message("Clients retrieved successfully")
                .data(userService.search(q))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Client retrieved successfully")
                .data(userService.getById(id))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true)
                .message("Client updated successfully")
                .data(userService.update(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Client deleted successfully")
                .build());
    }
}
