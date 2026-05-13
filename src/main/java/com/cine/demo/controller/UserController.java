package com.cine.demo.controller;

import com.cine.demo.dto.request.UpdateUserRequestDTO;
import com.cine.demo.dto.request.UserRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.UserResponseDTO;
import com.cine.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Registered user management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true).message("Users retrieved successfully").data(userService.getAll()).build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name or email")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.<List<UserResponseDTO>>builder()
                .success(true).message("Search results").data(userService.search(q)).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true).message("User retrieved successfully").data(userService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponseDTO>builder()
                        .success(true).message("User created successfully").data(userService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true).message("User updated successfully").data(userService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User deleted successfully").build());
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Upload profile image")
    public ResponseEntity<ApiResponse<UserResponseDTO>> uploadImage(
            @PathVariable Long id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.<UserResponseDTO>builder()
                .success(true).message("Image uploaded successfully").data(userService.uploadImage(id, file)).build());
    }
}
