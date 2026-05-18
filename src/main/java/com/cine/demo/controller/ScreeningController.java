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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
@Tag(name = "Screenings", description = "Scheduled movie sessions in theaters")
public class ScreeningController {

    private final ScreeningService screeningService;
    private final PurchaseService purchaseService;

    @GetMapping
    @Operation(summary = "List all screenings, optionally filtered by date (YYYY-MM-DD)")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getAll(
            @RequestParam(required = false) LocalDate date) {
        List<ScreeningResponseDTO> result = date != null
                ? screeningService.getByDate(date)
                : screeningService.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Screenings retrieved successfully", result));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "List upcoming screenings")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getUpcoming() {
        return ResponseEntity.ok(ApiResponse.ok("Upcoming screenings retrieved successfully", screeningService.getUpcoming()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get screening by ID")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Screening retrieved successfully", screeningService.getById(id)));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "List screenings for a movie")
    public ResponseEntity<ApiResponse<List<ScreeningResponseDTO>>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.ok("Movie screenings retrieved successfully", screeningService.getByMovie(movieId)));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<ScreeningSeatResponseDTO>>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Screening seats retrieved successfully", screeningService.getSeats(id)));
    }

    @PostMapping
    @Operation(summary = "Create new screening")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> create(@Valid @RequestBody ScreeningRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Screening created successfully", screeningService.create(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update screening")
    public ResponseEntity<ApiResponse<ScreeningResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateScreeningRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Screening updated successfully", screeningService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete screening")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Screening deleted successfully"));
    }

    @PostMapping("/{id}/sync-seats")
    @Operation(summary = "Sync seats for a screening with current theater layout")
    public ResponseEntity<ApiResponse<List<ScreeningSeatResponseDTO>>> syncSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Seats synchronized successfully", screeningService.syncSeats(id)));
    }

    @PostMapping("/{id}/seats/{seatId}/reserve")
    @Operation(summary = "Reserve seat in a screening")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> reserveSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.ok("Seat reserved successfully", screeningService.reserveSeat(id, seatId)));
    }

    @PostMapping("/{id}/seats/{seatId}/release")
    @Operation(summary = "Release seat reservation")
    public ResponseEntity<ApiResponse<ScreeningSeatResponseDTO>> releaseSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(ApiResponse.ok("Seat reservation released successfully", screeningService.releaseSeat(id, seatId)));
    }

    @GetMapping("/{id}/purchases")
    @Operation(summary = "List purchases for a screening")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDTO>>> getPurchases(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Screening purchases retrieved successfully", purchaseService.getByScreening(id)));
    }
}
