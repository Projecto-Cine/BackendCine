package com.cine.demo.controller;

import com.cine.demo.dto.request.ScreeningRequestDTO;
import com.cine.demo.dto.request.UpdateScreeningRequestDTO;
import com.cine.demo.dto.response.PurchaseResponseDTO;
import com.cine.demo.dto.response.ScreeningResponseDTO;
import com.cine.demo.dto.response.ScreeningSeatResponseDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.service.PurchaseService;
import com.cine.demo.service.ScreeningService;
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
public class ScreeningController {

    private final ScreeningService screeningService;
    private final PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<ScreeningResponseDTO>> getAll(
            @RequestParam(required = false) LocalDate date) {
        if (date != null) {
            return ResponseEntity.ok(screeningService.getByDate(date));
        }
        return ResponseEntity.ok(screeningService.getAll());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ScreeningResponseDTO>> getUpcoming() {
        return ResponseEntity.ok(screeningService.getUpcoming());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getById(id));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ScreeningResponseDTO>> getByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(screeningService.getByMovie(movieId));
    }

    @PostMapping
    public ResponseEntity<ScreeningResponseDTO> create(@Valid @RequestBody ScreeningRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(screeningService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreeningResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody UpdateScreeningRequestDTO dto) {
        return ResponseEntity.ok(screeningService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        screeningService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/seats/{seatId}/reserve")
    public ResponseEntity<ScreeningSeatResponseDTO> reserveSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(screeningService.reserveSeat(id, seatId));
    }

    @PostMapping("/{id}/seats/{seatId}/release")
    public ResponseEntity<ScreeningSeatResponseDTO> releaseSeat(
            @PathVariable Long id, @PathVariable Long seatId) {
        return ResponseEntity.ok(screeningService.releaseSeat(id, seatId));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getSeatsByScreening(id));
    }

    @GetMapping("/{id}/purchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getPurchases(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getByScreening(id));
    }
}