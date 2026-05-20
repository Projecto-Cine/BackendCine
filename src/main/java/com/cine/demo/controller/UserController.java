package com.cine.demo.controller;

import com.cine.demo.dto.request.QuickRegisterDTO;
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
    @Operation(summary = "List all users, optionally filtered by membership")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAll(
            @RequestParam(required = false) Boolean member) {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved successfully", userService.getAll(member)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name or email")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Search results", userService.search(q)));
    }

    @GetMapping("/by-email")
    @Operation(summary = "Find user by email")
    public ResponseEntity<ApiResponse<UserResponseDTO>> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok("User found", userService.findByEmail(email)));
    }

    @PostMapping("/quick-register")
    @Operation(summary = "Quick register a new user during purchase")
    public ResponseEntity<ApiResponse<UserResponseDTO>> quickRegister(@Valid @RequestBody QuickRegisterDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", userService.quickRegister(dto)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved successfully", userService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", userService.create(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("User updated successfully", userService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully"));
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Upload profile image")
    public ResponseEntity<ApiResponse<UserResponseDTO>> uploadImage(
            @PathVariable Long id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Image uploaded successfully", userService.uploadImage(id, file)));
    }
}
