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
@Tag(name = "Asientos", description = "Gestión de asientos individuales")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "Listar todos los asientos")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<SeatResponseDTO>>builder()
                .success(true).message("Asientos obtenidos correctamente").data(seatService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asiento por ID")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Asiento obtenido correctamente").data(seatService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Crear asiento")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> create(@Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SeatResponseDTO>builder()
                        .success(true).message("Asiento creado correctamente").data(seatService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asiento")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSeatRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Asiento actualizado correctamente").data(seatService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asiento")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Asiento eliminado correctamente").build());
    }
}
