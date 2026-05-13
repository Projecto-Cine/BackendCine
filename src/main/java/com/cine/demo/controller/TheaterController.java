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
        return ResponseEntity.ok(ApiResponse.ok("Theaters retrieved successfully", theaterService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get theater by ID")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Theater retrieved successfully", theaterService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Create new theater")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> create(@Valid @RequestBody TheaterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Theater created successfully", theaterService.create(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update theater")
    public ResponseEntity<ApiResponse<TheaterResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTheaterRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Theater updated successfully", theaterService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete theater")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Theater deleted successfully"));
    }

    @GetMapping("/{id}/seats")
    @Operation(summary = "List seats in a theater")
    public ResponseEntity<ApiResponse<List<SeatResponseDTO>>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Seats retrieved successfully", seatService.getByTheater(id)));
    }
}
