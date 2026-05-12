package com.cine.demo.controller;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Individual seat management")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "List all seats")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<SeatResponseDTO>>builder()
                .success(true).message("Seats retrieved successfully").data(seatService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seat by ID")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Seat retrieved successfully").data(seatService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Create seat")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> create(@Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SeatResponseDTO>builder()
                        .success(true).message("Seat created successfully").data(seatService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update seat")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSeatRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Seat updated successfully").data(seatService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seat")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Seat deleted successfully").build());
    }
}
