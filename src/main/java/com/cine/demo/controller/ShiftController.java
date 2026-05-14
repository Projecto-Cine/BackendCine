package com.cine.demo.controller;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.service.ShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Tag(name = "Shifts", description = "Employee work shift management")
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    @Operation(summary = "List all shifts")
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok("Shifts retrieved successfully", shiftService.findAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shift by ID")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Shift retrieved successfully", shiftService.findById(id)));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "List shifts for a specific date")
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok("Shifts for date retrieved successfully", shiftService.findByDate(date)));
    }

    @GetMapping("/range")
    @Operation(summary = "List shifts in a date range", description = "Parameters: from and to in yyyy-MM-dd format")
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok("Shifts in range retrieved successfully", shiftService.findByDateRange(from, to)));
    }

    @PostMapping
    @Operation(summary = "Create new shift")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(@Valid @RequestBody ShiftRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Shift created successfully", shiftService.save(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shift")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> update(
            @PathVariable Long id, @RequestBody UpdateShiftRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Shift updated successfully", shiftService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shift")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        shiftService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Shift deleted successfully"));
    }
}
