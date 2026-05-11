package com.cine.demo.controller;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.ApiResponse;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
@Tag(name = "Screenings", description = "Scheduled movie sessions in theaters")
public class ScreeningController {

    private final ScreeningService screeningService;
    private final PurchaseService purchaseService;

    @GetMapping
    @Operation(summary = "List all screenings")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Screenings retrieved successfully").data(screeningService.getAll()).build());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "List upcoming screenings")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getUpcoming() {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Upcoming screenings retrieved successfully").data(screeningService.getUpcoming()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get screening by ID")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<ScreeningResponseDTO>builder()
                .success(true).message("Screening retrieved successfully").data(screeningService.getById(id)).build());
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "List screenings for a movie")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.<List<ScreeningResponseDTO>>builder()
                .success(true).message("Movie screenings retrieved successfully").data(screeningService.getByMovie(movieId)).build());
    }

    @PostMapping
    @Operation(summary = "Create new screening")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> create(@Valid @RequestBody ScreeningRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ScreeningResponseDTO>builder()
                        .success(true).message("Screening created successfully").data(screeningService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update screening")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateScreeningRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<ScreeningResponseDTO>builder()
                .success(true).message("Screening updated successfully").data(screeningService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete screening")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Screening deleted successfully").build());
    }

    @PostMapping("/{id}/seats/{seatId}/reserve")
    @Operation(summary = "Reserve a seat in a screening")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> reserveSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.<ScreeningSeatResponseDTO>builder()
                .success(true).message("Seat reserved successfully").data(screeningService.reserveSeat(id, seatId)).build());
    }

    @PostMapping("/{id}/seats/{seatId}/release")
    @Operation(summary = "Release a seat reservation")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> releaseSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.<ScreeningSeatResponseDTO>builder()
                .success(true).message("Seat reservation released successfully").data(screeningService.releaseSeat(id, seatId)).build());
    }

    @GetMapping("/{id}/purchases")
    @Operation(summary = "List purchases for a screening")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getPurchases(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<List<PurchaseResponseDTO>>builder()
                .success(true).message("Screening purchases retrieved successfully")
                .data(purchaseService.getByScreening(id)).build());
    }
}
