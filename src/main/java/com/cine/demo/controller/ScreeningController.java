package com.cine.demo.controller;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;
    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Proyecciones obtenidas correctamente").data(screeningService.getAll()).build());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getUpcoming() {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Próximas proyecciones obtenidas correctamente").data(screeningService.getUpcoming()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ScreeningResponseDTO>builder()
                .success(true).message("Proyección obtenida correctamente").data(screeningService.getById(id)).build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Proyecciones de la película obtenidas correctamente").data(screeningService.getByMovie(movieId)).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> create(@Valid @RequestBody ScreeningRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ScreeningResponseDTO>builder()
                        .success(true).message("Proyección creada correctamente").data(screeningService.create(dto)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateScreeningRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ScreeningResponseDTO>builder()
                .success(true).message("Proyección actualizada correctamente").data(screeningService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Proyección eliminada correctamente").build());
    }

    @PostMapping("/{id}/seats/{seatId}/reserve")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> reserveSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.<ScreeningSeatResponseDTO>builder()
                .success(true).message("Asiento reservado correctamente").data(screeningService.reserveSeat(id, seatId)).build());
    }

    @GetMapping("/{id}/purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getPurchases(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true).message("Compras de la proyección obtenidas correctamente")
                .data(purchaseService.getByScreening(id)).build());
    }

    @PostMapping("/{id}/seats/{seatId}/release")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> releaseSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.<ScreeningSeatResponseDTO>builder()
                .success(true).message("Reserva cancelada correctamente").data(screeningService.releaseSeat(id, seatId)).build());
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getSeatsByScreening(id));
    }
}
