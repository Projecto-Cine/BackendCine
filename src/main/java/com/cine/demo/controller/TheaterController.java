package com.cine.demo.controller;

import com.cine.demo.dto.request.TheaterRequestDTO;
import com.cine.demo.dto.request.UpdateTheaterRequestDTO;
import com.cine.demo.dto.response.SeatResponseDTO;
import com.cine.demo.dto.response.TheaterResponseDTO;
import com.cine.demo.service.SeatService;
import com.cine.demo.service.TheaterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;
    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<List<TheaterResponseDTO>> getAll() {
        return ResponseEntity.ok(theaterService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(theaterService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TheaterResponseDTO> create(@Valid @RequestBody TheaterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(theaterService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTheaterRequestDTO dto) {
        return ResponseEntity.ok(theaterService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.getByTheater(id));
    }
}