package com.cine.demo.controller;

import com.cine.demo.dto.request.ShiftRequestDTO;
import com.cine.demo.dto.request.UpdateShiftRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.ShiftResponseDTO;
import com.cine.demo.service.ShiftService;
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
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<ShiftResponseDTO>>builder()
                .success(true)
                .message("Turnos obtenidos correctamente")
                .data(shiftService.findAll())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true)
                .message("Turno obtenido correctamente")
                .data(shiftService.findById(id))
                .build());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.<List<ShiftResponseDTO>>builder()
                .success(true)
                .message("Turnos del día obtenidos correctamente")
                .data(shiftService.findByDate(date))
                .build());
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.<List<ShiftResponseDTO>>builder()
                .success(true)
                .message("Turnos en rango obtenidos correctamente")
                .data(shiftService.findByDateRange(from, to))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> create(@Valid @RequestBody ShiftRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ShiftResponseDTO>builder()
                        .success(true)
                        .message("Turno creado correctamente")
                        .data(shiftService.save(dto))
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody UpdateShiftRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ShiftResponseDTO>builder()
                .success(true)
                .message("Turno actualizado correctamente")
                .data(shiftService.update(id, dto))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        shiftService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Turno eliminado correctamente")
                .build());
    }
}
