package com.cine.demo.controller;

import com.cine.demo.dto.request.SeatRequestDTO;
import com.cine.demo.dto.request.UpdateSeatRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<SeatResponseDTO>>builder()
                .success(true).message("Asientos obtenidos correctamente").data(seatService.getAll()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Asiento obtenido correctamente").data(seatService.getById(id)).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeatResponseDTO>> create(@Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SeatResponseDTO>builder()
                        .success(true).message("Asiento creado correctamente").data(seatService.create(dto)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSeatRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<SeatResponseDTO>builder()
                .success(true).message("Asiento actualizado correctamente").data(seatService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Asiento eliminado correctamente").build());
    }
}
