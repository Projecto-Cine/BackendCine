package com.cine.demo.controller;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.service.SeatService;
import com.cine.demo.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Gestión de salas de cine y sus asientos")
public class TheaterController {

    private final TheaterService theaterService;
    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "Listar todas las salas")
    public ResponseEntity<ApiResponse<List<TheaterResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<TheaterResponseDTO>>builder()
                .success(true).message("Salas obtenidas correctamente").data(theaterService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sala por ID")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TheaterResponseDTO>builder()
                .success(true).message("Sala obtenida correctamente").data(theaterService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva sala")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> create(@Valid @RequestBody TheaterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TheaterResponseDTO>builder()
                        .success(true).message("Sala creada correctamente").data(theaterService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sala")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTheaterRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<TheaterResponseDTO>builder()
                .success(true).message("Sala actualizada correctamente").data(theaterService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sala")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Sala eliminada correctamente").build());
    }

    @GetMapping("/{id}/seats")
    @Operation(summary = "Listar asientos de una sala")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<List<SeatResponseDTO>>builder()
                .success(true).message("Asientos obtenidos correctamente").data(seatService.getByTheater(id)).build());
    }
}
