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
@Tag(name = "Theaters", description = "Cinema theater management and seating")
public class TheaterController {

    private final TheaterService theaterService;
    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "List all theaters")
    public ResponseEntity<ApiResponse<List<TheaterResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<TheaterResponseDTO>>builder()
                .success(true).message("Theaters retrieved successfully").data(theaterService.getAll()).build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get theater by ID")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<TheaterResponseDTO>builder()
                .success(true).message("Theater retrieved successfully").data(theaterService.getById(id)).build());
    }

    @PostMapping
    @Operation(summary = "Create new theater")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> create(@Valid @RequestBody TheaterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<TheaterResponseDTO>builder()
                        .success(true).message("Theater created successfully").data(theaterService.create(dto)).build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update theater")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTheaterRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.<TheaterResponseDTO>builder()
                .success(true).message("Theater updated successfully").data(theaterService.update(id, dto)).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete theater")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Theater deleted successfully").build());
    }

    @GetMapping("/{id}/seats")
    @Operation(summary = "List seats in a theater")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.<List<SeatResponseDTO>>builder()
                .success(true).message("Seats retrieved successfully").data(seatService.getByTheater(id)).build());
    }
}
